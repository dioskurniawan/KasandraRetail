package id.kasandra.retail;

import android.graphics.Color;
import android.text.Html;

import id.kasandra.retail.welcome.WelcomeActivity;
import id.kasandra.retail.welcome.WelcomeScreenBuilder;
import id.kasandra.retail.welcome.WelcomeScreenConfiguration;

public class SampleWelcomeActivity extends WelcomeActivity {

    private SessionManager session;

    @Override
    protected WelcomeScreenConfiguration configuration() {
        session = new SessionManager(this.getApplicationContext());
        if(session.getShowcase()){
            finish();
        }
            return new WelcomeScreenBuilder(this)
                    .theme(R.style.SampleWelcomeScreenTheme)
                    .defaultBackgroundColor(R.color.colorPrimary)
                    .defaultTitleTypefacePath("Montserrat-Bold.ttf")
                    .defaultHeaderTypefacePath("Montserrat-Bold.ttf")
                    .basicPage(R.drawable.screen1, "Selamat datang di KASANDRA!", "KASANDRA adalah solusi tepat untuk Point of Sale (POS) usaha Anda. KASANDRA terdiri dari KASANDRA Retail untuk aplikasi kasir, KASANDRA Web untuk pengelolaan dan pelaporan, dan KASANDRA Boss untuk memantau usaha Anda dari mana saja. KASANDRA dapat Anda pergunakan secara gratis!", R.color.lightgreen1)
                    .basicPage(R.drawable.screen2, "Langkah 1 : Buat akun Pengguna", "Buatlah akun Pengguna khusus untuk Anda pribadi. Jangan bagikan password Anda kepada siapa pun.\n" +
                            "Apabila Anda memiliki karyawan, mintalah mereka untuk membuat akun Pengguna masing-masing.", R.color.purple_background)
                    .basicPage(R.drawable.screen3, "Langkah 2 : Buat akun Usaha Anda", "Apabila Anda adalah pemilik usaha, Anda perlu membuat akun Usaha Anda (toko, restoran, perusahaan). Setelah itu, Anda bisa mendaftarkan akun karyawan-karyawan Anda ke dalam akun Usaha Anda.", R.color.red_background)
                    .basicPage(R.drawable.screen4, "Langkah 3 : Buat outlet dan masukkan produk-produk usaha Anda", "Masuklah ke KASANDRA Web dengan akun Pengguna yang baru Anda buat, kemudian daftarkanlah nama outlet usaha Anda (dapat lebih dari satu outlet). Setelah itu Anda dapat memasukkan produk-produk Anda beserta harganya.", R.color.lightaqua)
                    .basicPage(R.drawable.screen5, "Mulai Usaha Anda!", "Hidupkan KASANDRA Retail dan sambungkan printer Bluetooth, dan dalam sekejap Anda telah siap berdagang.\n" +
                            "Unduh KASANDRA Boss ke ponsel Anda dan pantau penjualan usaha Anda secara online di mana saja Anda berada.", R.color.colorPrimary)
                    .swipeToDismiss(true)
                    .exitAnimation(android.R.anim.fade_out)
                    .build();
    }

    public static String welcomeKey() {
        return "WelcomeScreen";
    }

}
