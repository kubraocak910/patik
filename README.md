# 🧦 Patik — Çorap ve Ayakkabı Endeksli Hava Durumu Uygulaması

**Patik**, klasik hava durumu uygulamalarının ötesine geçerek kullanıcılara anlık hava koşullarına (sıcaklık, yağış, kar, rüzgar) göre **en uygun çorap ve ayakkabı tavsiyesini** sunan modern bir Android uygulamasıdır.

---

## ✨ Temel Özellikler

- **📍 Akıllı Konum Tespiti:** Uygulama açılışında izin alarak cihazın mevcut konumundaki şehri otomatik bulur ve hava durumunu getirir (İzin verilmezse varsayılan olarak Ankara ile başlar).
- **🧦 Görsel Çorap ve Ayakkabı Eşleşmesi:**
  - 3D özel konsept illüstrasyonlar (`res/drawable-nodpi/`)
  - Hava durumuna göre dinamik başlıklar (*"Bot ve Kalın Çorap Havası"*, *"Babet Çorap Havası"*, *"Su Geçirmez Ayakkabı Havası"* vb.)
  - Eğlenceli ve pratik giyim ipuçları.
- **🎨 Mevsime Duyarlı Dinamik Renk Paletleri:**
  - ❄️ **Kış:** Buz mavisi, soğuk gri ve kar beyazı tonları.
  - 🌸 **İlkbahar:** Taze yeşil ve soft pembe tonları.
  - ☀️ **Yaz:** Güneş sarısı ve canlı turuncu tonları.
  - 🍂 **Sonbahar:** Sıcak kahverengi ve bordo tonları.
  - **🌐 Otomatik Yarımküre Tespiti:** Seçilen şehrin coğrafi enlemine ve bulunulan aya göre mevsim rengi otomatik uyarlanır (Örn: Türkiye'de kışken Avustralya'da yaz mevsimi algılanır). Tercihler `SharedPreferences` ile kalıcı saklanır.
- **🔍 Şehir Arama ve Anlık Önizleme:**
  - Open-Meteo Geocoding servisi ile dünya genelinde anlık arama.
  - Şehre tıklandığında ekleme yapmadan **anında güncel dereceyi ve çorap tavsiyesini** gösteren hızlı önizleme penceresi.
  - Şehri **"⭐ Ana Şehir"** yapma veya **"➕ Karşılaştırmaya Ekle"** seçenekleri.
- **📱 Akıllı Uyarlanabilir Hero Kart Alanı (1 - 4 Şehir):**
  - Karşılaştırmaya eklenen 3 ekstra şehir (toplam 4 şehir), tek bir kartın sığdığı aynı ~350dp'lik alana **2x2 grid** düzeninde sığar.
  - Kartlar arasında seçim yapılarak seçilen şehrin 24 saatlik ve 7 günlük detaylı tahminleri incelenebilir.
- **📊 Detaylı Meteorolojik Tahminler:**
  - 24 saatlik saatlik durum çubukları.
  - 7 günlük haftalık sıcaklık ve durum tahminleri.
  - Hissedilen sıcaklık, nem oranı ve rüzgar hızı göstergeleri.

---

## 🏛️ Mimari ve Teknolojiler

Proje, Google'ın modern Android geliştirme prensiplerine ve **Clean Architecture + MVVM** prensiplerine tam uyumlu olarak geliştirilmiştir:

- **Dil:** Kotlin 2.x
- **Kullanıcı Arayüzü:** Jetpack Compose (Material 3)
- **Durum Yönetimi:** `ViewModel` + `StateFlow` (Unidirectional Data Flow - UDF)
- **Eşzamanlılık:** Kotlin Coroutines (`async`, `awaitAll`, `Dispatchers.IO`)
- **Ağ Katmanı:** Retrofit 2 + Gson Converter
- **API Servisleri:** [Open-Meteo](https://open-meteo.com/) — *Açık kaynaklı, ücretsiz ve API anahtarı (Key) gerektirmeyen REST API.*
- **Kalıcı Depolama:** Android `SharedPreferences` (Ana şehir, eklenen şehirler ve tema tercihleri için)

### 📂 Proje Paket Yapısı

```
com.example.patik/
├── data/                       # Veri Katmanı
│   ├── ApiClient.kt            # Retrofit istemcileri
│   ├── CityApi.kt              # Geocoding arama API arayüzü
│   ├── ForecastApi.kt          # Hava tahmini API arayüzü
│   ├── LocationRepository.kt   # Cihaz konum sağlayıcısı
│   ├── SavedCitiesRepository.kt# Kalıcı şehir tercihleri
│   └── WeatherRepository.kt    # Hava durumu repository uygulaması
├── model/                      # Veri Modelleri
│   ├── City.kt                 # Şehir veri modeli
│   ├── Forecast.kt             # Anlık, saatlik ve haftalık tahmin modelleri
│   ├── Season.kt               # Mevsim mantığı ve yarımküre hesaplama
│   ├── SockAdvice.kt           # Çorap tavsiye karar matrisi
│   └── WeatherCondition.kt     # WMO kodları ve hava durum eşleştirmeleri
└── ui/                         # Arayüz Katmanı
    ├── component/              # Yeniden kullanılabilir Compose bileşenleri
    │   ├── ForecastDetails.kt  # Saatlik ve günlük tahmin listeleri
    │   ├── PatikSearchBar.kt   # Arama ve hızlı önizleme diyaloğu
    │   ├── SockDrawableMap.kt  # Çorap tiplerinin görsel eşleştirmesi
    │   └── SockHeroSection.kt  # 1-4 şehir arası uyarlanabilir görsel kart alanı
    ├── home/
    │   ├── HomeScreen.kt       # Ana ekran bileşeni
    │   └── HomeViewModel.kt    # Ana ekran durum ve iş mantığı yöneticisi
    └── theme/
        ├── Color.kt            # Mevsimlere özel renk paletleri
        ├── Theme.kt            # Mevsime duyarlı Material 3 teması
        └── Type.kt             # Tipografi tanımları
```

---

## 🚀 Nasıl Çalıştırılır?

1. Projeyi bilgisayarınıza klonlayın:
   ```bash
   git clone https://github.com/kubraocak910/patik.git
   ```
2. **Android Studio** ile projeyi açın.
3. Gradle senkronizasyonunun tamamlanmasını bekleyin.
4. Üst araç çubuğundaki yeşil **Run (▶️)** butonuna basarak bir emülatörde veya gerçek cihazda çalıştırın.

> **Not:** Uygulama herhangi bir API anahtarı yapılandırması gerektirmez; internet bağlantısı olan her cihazda doğrudan çalışır.
