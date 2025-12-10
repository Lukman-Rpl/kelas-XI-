package com.example.aplikasimonitoringkelas

import com.example.aplikasimonitoringkelas.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ================= AUTH =================
    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<UserData>>


    // ================= USERS =================
    @GET("users")
    suspend fun getUsers(): Response<ApiResponse<List<UserData>>>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Int): Response<ApiResponse<UserData>>

    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: Int, @Body user: UserData): Response<ApiResponse<UserData>>

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: Long): Response<ApiResponse<Unit>>


    // ================= JADWAL (FULL ARRAY SUPPORT) =================

    // GET FILTER → SELALU MENGEMBALIKAN ARRAY
    @GET("jadwal")
    suspend fun getJadwalFiltered(
        @Query("kelas_id") kelasId: Long? = null,
        @Query("hari") hari: String? = null,
        @Query("tanggal") tanggal: String? = null,
        @Query("status") status: String? = null
    ): Response<ApiResponse<List<JadwalItem>>>   // <-- ARRAY

    // GET BY ID → tetap single object
    @GET("jadwal/{id}")
    suspend fun getJadwalById(@Path("id") id: Long): Response<ApiResponse<JadwalItem>>

    // CREATE → kirim 1, terima object
    @POST("jadwal")
    suspend fun createJadwal(@Body request: JadwalCreateRequest): Response<ApiResponse<JadwalItem>>

    // UPDATE → kirim 1, terima object
    @PUT("jadwal/{id}")
    suspend fun updateJadwal(
        @Path("id") id: Long,
        @Body request: JadwalCreateRequest
    ): Response<ApiResponse<JadwalItem>>

    @DELETE("jadwal/{id}")
    suspend fun deleteJadwal(@Path("id") id: Long): Response<ApiResponse<Unit>>


    // ================= GURU =================
    @GET("guru")
    suspend fun getGuruList(): Response<GuruResponse>

    @GET("guru/{id}")
    suspend fun getGuruById(@Path("id") id: Long): Response<ApiResponse<GuruItem>>

    @GET("guru/mapel/{id_mapel}")
    suspend fun getGuruByMapel(@Path("id_mapel") idMapel: Long): Response<ApiResponse<List<GuruItem>>>

    @POST("guru")
    suspend fun createGuru(@Body request: GuruItem): Response<ApiResponse<GuruItem>>

    @PUT("guru/{id}")
    suspend fun updateGuru(@Path("id") id: Long, @Body request: GuruItem): Response<ApiResponse<GuruItem>>

    @DELETE("guru/{id}")
    suspend fun deleteGuru(@Path("id") id: Long): Response<ApiResponse<Unit>>


    // ================= IJIN =================
    @GET("ijin")
    suspend fun getAllIjin(): Response<ApiResponse<List<IjinItem>>>

    @POST("ijin")
    suspend fun createIjin(@Body request: IjinCreateRequest): Response<ApiResponse<IjinItem>>

    @GET("ijin/{id}")
    suspend fun getIjinById(@Path("id") id: Long): Response<ApiResponse<IjinItem>>

    @PUT("ijin/{id}")
    suspend fun updateIjin(@Path("id") id: Long, @Body request: IjinCreateRequest): Response<ApiResponse<IjinItem>>

    @DELETE("ijin/{id}")
    suspend fun deleteIjin(@Path("id") id: Long): Response<ApiResponse<Unit>>


    // ================= MAPEL =================
    @GET("mapel")
    suspend fun getMapelList(): Response<ApiResponse<List<MapelItem>>>

    @GET("mapel/{id}")
    suspend fun getMapelById(@Path("id") id: Long): Response<ApiResponse<MapelItem>>

    @POST("mapel")
    suspend fun createMapel(@Body request: MapelCreateRequest): Response<ApiResponse<MapelItem>>

    @PUT("mapel/{id}")
    suspend fun updateMapel(@Path("id") id: Long, @Body request: MapelCreateRequest): Response<ApiResponse<MapelItem>>

    @DELETE("mapel/{id}")
    suspend fun deleteMapel(@Path("id") id: Long): Response<ApiResponse<Unit>>


    // ================= KELAS =================
    @GET("kelas")
    suspend fun getKelasList(): Response<ApiResponse<List<KelasItem>>>

    @GET("kelas/{id}")
    suspend fun getKelasById(@Path("id") id: Long): Response<ApiResponse<KelasItem>>

    @POST("kelas")
    suspend fun createKelas(@Body request: KelasCreateRequest): Response<ApiResponse<KelasItem>>

    @PUT("kelas/{id}")
    suspend fun updateKelas(@Path("id") id: Long, @Body request: KelasCreateRequest): Response<ApiResponse<KelasItem>>

    @DELETE("kelas/{id}")
    suspend fun deleteKelas(@Path("id") id: Long): Response<ApiResponse<Unit>>


    // ================= GURU PENGGANTI =================
    @GET("guru-pengganti")
    suspend fun getGuruPenggantiList(): Response<ApiResponse<List<GuruPenggantiItem>>>

    @GET("guru-pengganti/{id}")
    suspend fun getGuruPenggantiById(@Path("id") id: Long): Response<ApiResponse<GuruPenggantiItem>>

    @POST("guru-pengganti")
    suspend fun createGuruPengganti(@Body request: GuruPenggantiItem): Response<ApiResponse<GuruPenggantiItem>>

    @PUT("guru-pengganti/{id}")
    suspend fun updateGuruPengganti(@Path("id") id: Long, @Body request: GuruPenggantiItem): Response<ApiResponse<GuruPenggantiItem>>

    @DELETE("guru-pengganti/{id}")
    suspend fun deleteGuruPengganti(@Path("id") id: Long): Response<ApiResponse<Unit>>
}
