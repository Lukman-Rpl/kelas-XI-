<?php
namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use App\Models\Categories;
use App\Models\GroupInvite;
use App\Models\Transaction;
use App\Models\Wallet;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Str;
use Midtrans\Config;
use Midtrans\Snap;

class WalletController extends Controller
{
    /**
     * Get all wallets
     */
    public function index()
{
    $userId = Auth::id();

    // Ambil semua dompet (Personal & Group) dalam 1 query
    $wallets = Wallet::with(['group']) // Eager load relasi grup
        ->where(function ($query) use ($userId) {
            // 1. Personal Wallet milik user
            $query->where(function ($q) use ($userId) {
                $q->where('type', 'personal')
                  ->where('user_id', $userId);
            })
            // 2. ATAU Group Wallet di mana user adalah Owner ATAU Member grup
            ->orWhere(function ($q) use ($userId) {
                $q->where('type', 'group')
                  ->whereHas('group', function ($g) use ($userId) {
                      $g->where('owner_id', $userId)
                        ->orWhereHas('members', function ($m) use ($userId) {
                            $m->where('users.id', $userId);
                        });
                  });
            });
        })
        ->latest()
        ->get();

    // Mapping response untuk memastikan kolom no_wallet dan data pendukung terstruktur rapi
    $formattedWallets = $wallets->map(function ($wallet) {
        return [
            'id'          => $wallet->id,
            'no_wallet'   => $wallet->no_wallet, // Nomor rekening/dompet unik
            'name'        => $wallet->name,
            'type'        => $wallet->type, // 'personal' atau 'group'
            'balance'     => (float) $wallet->balance,
            'group_id'    => $wallet->group_id,
            'group_name'  => $wallet->group ? $wallet->group->name : null,
            'created_at'  => $wallet->created_at->toDateTimeString(),
            'updated_at'  => $wallet->updated_at->toDateTimeString(),
        ];
    });

    return response()->json([
        'status'  => 'success',
        'message' => 'Berhasil mengambil daftar dompet.',
        'data'    => [
            // Memisahkan list agar tim mobile/Android mudah menampilkan di tab terpisah jika dibutuhkan
            'all'      => $formattedWallets,
            'personal' => $formattedWallets->where('type', 'personal')->values(),
            'group'    => $formattedWallets->where('type', 'group')->values(),
        ]
    ], 200);
}


    /**
     * Create a new wallet
     */
    public function store(Request $request)
{
    $request->validate([
        'name' => 'required|string|max:255',
        'type' => 'nullable|in:personal,group',
    ]);

    $type = $request->input('type', 'personal');
    $user = Auth::user();
    return DB::transaction(function () use ($request, $user, $type) {
        
        // Helper function internal untuk menghasilkan nomor wallet unik
        $generateNoWallet = function () {
            do {
                // Format: WAL-YYYYMMDD-XXXX (Contoh: WAL-20260826-A1B2)
                $noWallet = 'WAL-' . date('Ymd') . '-' . strtoupper(Str::random(4));
            } while (Wallet::where('no_wallet', $noWallet)->exists());

            return $noWallet;
        };

        if ($type === 'group') {
            // Pembuatan Group Wallet (Web)
            $group = \App\Models\Group::create([
                'name'     => $request->name,
                'owner_id' => $user->id,
            ]);

            // Buat wallet group beserta no_wallet unik
            $wallet = Wallet::create([
                'name'      => 'Dompet ' . $group->name,
                'no_wallet' => $generateNoWallet(), // <-- TAMBAHAN KHUSUS NO_WALLET
                'type'      => 'group',
                'group_id'  => $group->id,
                'user_id'   => null,
                'balance'   => 0,
                'is_active' => false, 
            ]);

            $group->users()->attach($user->id, ['role' => 'admin']);
            $group->invites()->create(['expired_at' => null, 'max_usage' => null]);
            $wallet->load('group.invites');

            return response()->json([
                'status'  => 'success',
                'message' => 'Dompet grup berhasil dibuat!',
                'data'    => $wallet
            ], 201);

        } else {
            // Pembuatan Personal Wallet (Android / Web)
            // STEP 1: Nonaktifkan semua dompet pribadi milik user terlebih dahulu
            Wallet::where('user_id', $user->id)
                ->where('type', 'personal')
                ->update(['is_active' => false]);

            // STEP 2: Buat dompet baru beserta no_wallet unik
            $wallet = Wallet::create([
                'name'      => $request->name,
                'no_wallet' => $generateNoWallet(), // <-- TAMBAHAN KHUSUS NO_WALLET
                'type'      => 'personal',
                'user_id'   => $user->id,
                'balance'   => $request->input('balance', 0.0),
            ]);

            return response()->json([
                'status'  => 'success',
                'message' => 'Dompet pribadi berhasil dibuat dan diaktifkan!',
                'data'    => $wallet
            ], 201);
        }
    });
}
    /**
     * Mengubah Dompet Aktif Pengguna
     * POST /api/wallets/{id}/activate
     */
    public function setActiveWallet($id)
    {
        $userId = Auth::id();

        $wallet = Wallet::where(function ($q) use ($userId) {
            $q->where('user_id', $userId)
              ->orWhereHas('group.users', function ($query) use ($userId) {
                  $query->where('users.id', $userId);
              });
        })->find($id);

        if (!$wallet) {
            return response()->json([
                'status'  => 'error',
                'message' => 'Dompet tidak ditemukan atau Anda tidak memiliki akses.'
            ], 404);
        }

        DB::transaction(function () use ($userId, $wallet) {
            // Nonaktifkan semua dompet personal milik user
            Wallet::where('user_id', $userId)->update(['is_active' => false]);

            // Aktifkan dompet terpilih
            $wallet->is_active = true;
            $wallet->save();
        });

        return response()->json([
            'status'  => 'success',
            'message' => 'Dompet ' . $wallet->name . ' berhasil diaktifkan.',
            'data'    => $wallet
        ], 200);
    }

    /**
     * Join Group Wallet via Kode Undangan / Link
     * POST /api/wallets/join
     */
    public function joinGroup(Request $request)
    {
        $request->validate([
            'code' => 'required|string',
        ]);

        $input = trim($request->code);

        if (filter_var($input, FILTER_VALIDATE_URL)) {
            $parsedUrl = parse_url($input);
            if (isset($parsedUrl['query'])) {
                parse_str($parsedUrl['query'], $queryParams);
                $code = $queryParams['code'] ?? $input;
            } else {
                $code = last(explode('/', $parsedUrl['path']));
            }
        } else {
            $code = $input;
        }

        $code = strtoupper(trim($code));
        $user = Auth::user();

        $invite = GroupInvite::with('group.wallet')->where('code', $code)->first();

        if (!$invite) {
            return response()->json([
                'status'  => 'error',
                'message' => 'Kode atau link undangan tidak ditemukan.'
            ], 404);
        }

        if ($invite->expired_at && now()->greaterThan($invite->expired_at)) {
            return response()->json([
                'status'  => 'error',
                'message' => 'Kode undangan sudah kadaluarsa.'
            ], 410);
        }

        if ($invite->max_usage && $invite->used_count >= $invite->max_usage) {
            return response()->json([
                'status'  => 'error',
                'message' => 'Batas penggunaan kode undangan telah habis.'
            ], 410);
        }

        $group = $invite->group;

        if (!$group) {
            return response()->json([
                'status'  => 'error',
                'message' => 'Grup tidak ditemukan.'
            ], 404);
        }

        if ($group->users()->where('users.id', $user->id)->exists()) {
            return response()->json([
                'status'  => 'success',
                'message' => 'Anda sudah menjadi anggota dompet grup ini.',
                'data'    => $group->wallet
            ], 200);
        }

        $group->users()->attach($user->id, [
            'role' => 'member',
        ]);

        $invite->increment('used_count');

        $group->load('wallet');

        return response()->json([
            'status'  => 'success',
            'message' => 'Berhasil bergabung dengan grup ' . $group->name,
            'data'    => $group->wallet
        ], 200);
    }

    /**
     * Ambil Kode Undangan Aktif milik Wallet Grup tertentu
     * GET /api/wallets/{id}/invite-code
     */
    public function getInviteCode($walletId)
    {
    $user = Auth::user();

    // 1. Cari Wallet berdasarkan ID beserta relasi Group & Invite-nya
    $wallet = Wallet::with(['group.invites'])->find($walletId);

    if (!$wallet || !$wallet->group) {
        return response()->json([
            'status'  => 'error',
            'message' => 'Dompet grup tidak ditemukan.'
        ], 404);
    }

    $group = $wallet->group;

    // 2. Otorisasi: Cek apakah user terdaftar sebagai anggota di grup tersebut
    if (!$group->users()->where('users.id', $user->id)->exists()) {
        return response()->json([
            'status'  => 'error',
            'message' => 'Akses ditolak. Anda bukan anggota grup ini.'
        ], 403);
    }

    // 3. Ambil/Buat kode undangan
    $invite = $group->invites()->latest()->first();

    if (!$invite) {
        $invite = $group->invites()->create([
            'code'       => strtoupper(\Illuminate\Support\Str::random(6)),
            'expired_at' => null,
            'max_usage'  => null,
        ]);
    }

    return response()->json([
        'status'  => 'success',
        'message' => 'Berhasil mengambil kode undangan',
        'data'    => [
            'wallet_id'  => $wallet->id,
            'group_id'   => $group->id,
            'group_name' => $group->name,
            'code'       => $invite->code,
            'share_url'  => 'https://sakubijak.com/join?code=' . $invite->code
        ]
    ], 200);
 }

     public function transactions($id)
    {
        // Cari dompet berdasarkan ID
        $wallet = Wallet::find($id);

        if (!$wallet) {
            return response()->json([
                'status'  => false,
                'message' => 'Wallet tidak ditemukan',
                'data'    => null
            ], 404);
        }

        // Ambil relasi transaksi (pastikan relasi 'transactions' sudah didefinisikan di Model Wallet)
        $transactions = $wallet->transactions()->orderBy('created_at', 'desc')->get();

        return response()->json([
            'status'  => true,
            'message' => 'Berhasil mengambil daftar transaksi wallet',
            'data'    => $transactions
        ], 200);
    }

    public function topUp(Request $request)
    {
        // 1. Validasi Input
        $request->validate([
            'wallet_id'   => 'required|exists:wallets,id',
            'amount'      => 'required|numeric|min:10000',
            'description' => 'nullable|string|max:255',
        ]);
    
        $user = Auth::user();
    
        // 2. Cek Akses Dompet
        $wallet = Wallet::where('id', $request->wallet_id)
            ->where('user_id', $user->id)
            ->first();
    
        if (!$wallet) {
            return response()->json([
                'status'  => 'error',
                'message' => 'Dompet tidak ditemukan atau Anda tidak memiliki akses.'
            ], 403);
        }
    
        try {
            // Ambil Kategori 'Top Up'
            $topUpCategory = Categories::where('categories', 'Top Up')->first();
    
            // 3. Konfigurasi Midtrans Snap
            // Mengambil dari config/services.php dengan fallback ke env()
            $serverKey = config('services.midtrans.server_key') ?? env('MIDTRANS_SERVER_KEY');
            $isProduction = config('services.midtrans.is_production') ?? env('MIDTRANS_IS_PRODUCTION', false);
    
            // Pengecekan keamanan jika ServerKey tetap tidak ditemukan
            if (empty($serverKey)) {
                return response()->json([
                    'status'  => 'error',
                    'message' => 'Midtrans Server Key belum diatur di .env atau config/services.php'
                ], 500);
            }
    
            Config::$serverKey     = $serverKey;
            Config::$isProduction  = filter_var($isProduction, FILTER_VALIDATE_BOOLEAN);
            Config::$isSanitized   = true;
            Config::$is3ds         = true;
    
            // 4. Generate Unique Order ID
            $orderId = 'TOPUP-' . $wallet->id . '-' . time() . '-' . rand(100, 999);
    
            // 5. Simpan Transaksi Pending ke Database
            $transaction = Transaction::create([
                'order_id'    => $orderId,
                'user_id'     => $user->id,
                'wallet_id'   => $wallet->id,
                'category_id' => $topUpCategory ? $topUpCategory->id : null,
                'amount'      => $request->amount,
                'type'        => defined('App\Models\Transaction::TYPE_INCOME') ? Transaction::TYPE_INCOME : 'income',
                'status'      => 'pending',
                'date'        => now(),
                'description' => $request->description ?? ('Top Up Saldo ' . $wallet->name),
            ]);
    
            // 6. Buat Parameter untuk Midtrans Snap
            $params = [
                'transaction_details' => [
                    'order_id'     => $orderId,
                    'gross_amount' => (int) $request->amount,
                ],
                'customer_details' => [
                    'first_name' => $user->name ?? 'User',
                    'email'      => $user->email ?? 'user@example.com',
                ],
                'custom_field1' => (string) $wallet->id,
            ];
    
            // 7. Request Snap Token dari Midtrans
            $snapToken = Snap::getSnapToken($params);
    
            return response()->json([
                'status'  => 'success',
                'message' => 'Berhasil membuat transaksi top up',
                'data'    => [
                    'snap_token'     => $snapToken,
                    'order_id'       => $orderId,
                    'transaction_id' => $transaction->id
                ]
            ], 200);
    
        } catch (\Exception $e) {
            return response()->json([
                'status'  => 'error',
                'message' => 'Gagal memproses transaksi top up: ' . $e->getMessage()
            ], 500);
        }
    }

    /**
 * Display the specified wallet.
 */
   public function show($id)
   {
    $user = Auth::user();

    $wallet = Wallet::where('id', $id)
        ->where('user_id', $user->id)
        ->first();

    if (!$wallet) {
        return response()->json([
            'status'  => 'error',
            'message' => 'Dompet tidak ditemukan atau Anda tidak memiliki akses.'
        ], 404);
    }

    return response()->json([
        'status' => 'success',
        'data'   => $wallet
    ], 200);
   }
}