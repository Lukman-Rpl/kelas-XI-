<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\Transaction;
use App\Models\Wallet;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Log;
use Midtrans\Config;
use Midtrans\Notification;

class MidtransWebhookController extends Controller 
{
    public function handleNotification(Request $request) 
    {
        // 1. Konfigurasi Midtrans Server Key
        Config::$serverKey    = config('services.midtrans.server_key') ?? env('MIDTRANS_SERVER_KEY');
        Config::$isProduction = (bool) (config('services.midtrans.is_production') ?? env('MIDTRANS_IS_PRODUCTION', false));
        Config::$isSanitized  = true;
        Config::$is3ds        = true;

        try {
            $notif = new Notification();
        } catch (\Exception $e) {
            Log::error('Midtrans Notification Error: ' . $e->getMessage());
            return response()->json(['status' => 'error', 'message' => 'Invalid Notification Payload'], 400);
        }

        $transactionStatus = $notif->transaction_status;
        $type              = $notif->payment_type;
        $orderId           = $notif->order_id;
        $fraudStatus       = $notif->fraud_status;
        $grossAmount       = $notif->gross_amount;

        // 2. Validasi Signature Key
        $signatureKey = hash("sha512", $orderId . $notif->status_code . $grossAmount . Config::$serverKey);
        if ($signatureKey !== $notif->signature_key) {
            Log::warning("Midtrans Invalid Signature for Order ID: {$orderId}");
            return response()->json(['status' => 'error', 'message' => 'Invalid Signature'], 403);
        }

        // Cek kondisi sukses pembayaran
        $isSuccess = false;
        if ($transactionStatus == 'capture') {
            if ($type == 'credit_card') {
                $isSuccess = ($fraudStatus == 'accept');
            }
        } elseif ($transactionStatus == 'settlement') {
            $isSuccess = true;
        }

        if ($isSuccess) {
            // 3. Cari transaksi berdasarkan order_id
            $transaction = Transaction::where('order_id', $orderId)->first();

            // Idempotency Check: Jika transaksi sudah sukses, abaikan request duplikat
            if ($transaction && $transaction->status === 'success') {
                Log::info("Order ID {$orderId} already marked as success. Skipping.");
                return response()->json(['status' => 'OK', 'message' => 'Transaction already processed']);
            }

            // 4. Tentukan Wallet ID & User ID
            $userId = $notif->custom_field1 ?? null;

            if ($transaction) {
                $wallet = Wallet::find($transaction->wallet_id);
            } else {
                // Fallback: Jika transaksi tidak ada di DB, cari dompet berdasarkan custom_field1 (user_id)
                $wallet = Wallet::where('user_id', $userId)->first();
            }

            if (!$wallet) {
                Log::error("Wallet not found for Order ID: {$orderId}, User ID: {$userId}");
                return response()->json(['status' => 'error', 'message' => 'Wallet not found'], 422);
            }

            // 5. Eksekusi Tambah Saldo & Simpan/Update Transaksi
            try {
                DB::transaction(function () use ($transaction, $wallet, $grossAmount, $orderId, $userId) {
                    
                    if ($transaction) {
                        $transaction->update(['status' => 'success']);
                    } else {
                        // Buat record transaksi baru jika belum ada
                        Transaction::create([
                            'order_id'    => $orderId,
                            'user_id'     => $userId,
                            'wallet_id'   => $wallet->id,
                            'amount'      => $grossAmount,
                            'status'      => 'success',
                            'description' => 'Top Up via Midtrans'
                        ]);
                    }

                    // Tambahkan saldo dompet
                    $wallet->increment('balance', $grossAmount);
                });

                // Kirim Notifikasi
                $this->sendFcmNotification(
                    $wallet->user_id, 
                    "Top Up Berhasil!", 
                    "Saldo sebesar Rp " . number_format($grossAmount, 0, ',', '.') . " telah masuk ke dompet Anda."
                );

            } catch (\Exception $e) {
                Log::error("Failed processing top-up DB transaction for Order ID {$orderId}: " . $e->getMessage());
                return response()->json(['status' => 'error', 'message' => 'Database transaction failed'], 500);
            }
        } elseif (in_array($transactionStatus, ['deny', 'expire', 'cancel'])) {
            Transaction::where('order_id', $orderId)->update(['status' => 'failed']);
        }

        return response()->json(['status' => 'OK']);
    }

    private function sendFcmNotification($userId, $title, $message)
    {
        Log::info("FCM Sent to User {$userId}: {$title} - {$message}");
    }
}