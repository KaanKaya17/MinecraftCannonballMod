# Realistic Cannonball Mod

[**English**](../README.md) · [**GitHub Actions derlemeleri**](https://github.com/KaanKaya17/MinecraftCannonballMod/actions/workflows/build.yml?query=branch%3Amain)

Minecraft 1.20.1 için bu Fabric modu, binilip hareket ettirilebilen bir sahra topu, gülle ve harbi ekler. Gülle, çarptığı bloğa göre sekebilir ya da bloğu kırabilir. Topun kendi modeli, geri tepme animasyonu, sesleri ve duman efektleri vardır.

## Kurulum

1. Minecraft Java Edition **1.20.1**, **Fabric Loader 0.19.5 veya üzeri** ve **Java 17 veya üzerini** kur.
2. [Minecraft 1.20.1 için Fabric API](https://modrinth.com/mod/fabric-api/versions?g=1.20.1) dosyasını oyunun `mods` klasörüne ekle. Proje `0.92.12+1.20.1` ile derlenir.
3. Modu aşağıdaki adımlarla derle veya başarılı bir [GitHub Actions derlemesini](https://github.com/KaanKaya17/MinecraftCannonballMod/actions/workflows/build.yml?query=branch%3Amain) açıp **Artifacts** paketini erişime açıksa indir. İndirmek için GitHub hesabına giriş yapman gerekir.
4. `cannonball-1.0.0.jar` dosyasını `mods` klasörüne koy ve Fabric profiliyle oyunu aç. Ayrı bir sunucu kullanıyorsan modu ve Fabric API’yi hem sunucuya hem bağlanan oyunculara kur.

## Oynanış

- **Yerleştirme ve hareket.** Top eşyasını bir blok üzerinde kullan. Boş elle sağ tıklayarak topa bin. Fareyle nişan al, ileri ve geri tuşlarıyla hareket et ve eğilme tuşuyla in.
- **Doldurma ve ateşleme.** Sırayla bir ile üç barut ve bir demir gülle kullan. Harbiyle gülleyi sıkıştırabilirsin. Çakmak veya ateş topu gerekirse gülleyi sıkıştırır ve fitili yakar.
- **Kızıltaş.** Topun bulunduğu konumda veya altındaki blokta sinyal algılanırsa doldurulmuş ve sıkıştırılmış top ateşlenir. Minecraft’ın fırlatıcısı demir gülleyi doğrudan atabilir.

[Üretim tarifleri](../README.md#crafting) · [Tarif dosyaları](../src/main/resources/data/cannonball/recipes)

## Kaynaktan derleme

Depoyu klonla ve proje klasöründe Gradle Wrapper’ı çalıştır. GitHub Actions ile aynı derleme ortamı için **JDK 25** kullan. Derlenen mod Java 17’yi hedefler.

```powershell
git clone https://github.com/KaanKaya17/MinecraftCannonballMod.git
cd MinecraftCannonballMod
.\gradlew.bat build
```

macOS ve Linux’ta `./gradlew build` kullan. Kurulum dosyası `build/libs/cannonball-1.0.0.jar` konumundadır. Geliştirme istemcisini açmak için Gradle Wrapper ile `runClient` görevini çalıştır.

Hata bildirirken Minecraft, Fabric Loader ve Fabric API sürümlerini, tek oyunculu dünyada mı yoksa sunucuda mı karşılaştığını ve tekrar adımlarını ekle. [GitHub Issues](https://github.com/KaanKaya17/MinecraftCannonballMod/issues)

[MIT lisansı](../LICENSE) · Kaan Kaya
