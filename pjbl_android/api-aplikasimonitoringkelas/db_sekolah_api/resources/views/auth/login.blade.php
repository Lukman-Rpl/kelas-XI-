<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login Admin</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background: #eef2f7;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }
        .login-box {
            background: white;
            padding: 25px;
            border-radius: 8px;
            width: 350px;
            box-shadow: 0 4px 10px rgba(0,0,0,0.1);
        }
        input {
            width: 100%;
            padding: 10px;
            margin-top: 8px;
            border: 1px solid #ccc;
            border-radius: 5px;
        }
        button {
            margin-top: 12px;
            width: 100%;
            background: #4a6cf7;
            color: white;
            border: none;
            padding: 10px;
            border-radius: 5px;
        }
        .error { color: red; margin-top: 10px; font-size: 14px; }
        .success { color: green; margin-top: 10px; font-size: 14px; }
    </style>
</head>
<body>

<div class="login-box">
    <h2 style="text-align:center;">Login Admin</h2>

    @if(session('success'))
        <div class="success">{{ session('success') }}</div>
    @endif

    <form method="POST" action="{{ route('login.process') }}">
        @csrf

        <label>Email</label>
        <input type="email" name="email" value="{{ old('email') }}" required>
        @error('email') <div class="error">{{ $message }}</div> @enderror

        <label>Password</label>
        <input type="password" name="password" required>
        @error('password') <div class="error">{{ $message }}</div> @enderror

        <button type="submit">Login</button>
    </form>
</div>

</body>
</html>
