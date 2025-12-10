<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up()
{
    Schema::table('jadwal', function (Blueprint $table) {
        $table->string('sampai_jam', 255)->after('jam_ke');
    });
}

public function down()
{
    Schema::table('jadwal', function (Blueprint $table) {
        $table->dropColumn('sampai_jam');
    });
}

};
