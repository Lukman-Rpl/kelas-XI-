<?php

namespace App\Http\Controllers\Web\Admin;

use App\Http\Controllers\Controller;

class AuditLogController extends Controller
{
    public function index()
    {
        // nanti isi dengan audit_logs
        return view('admin.audit-logs.index');
    }
}
