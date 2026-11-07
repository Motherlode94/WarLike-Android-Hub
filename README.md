# WarLike Android Hub

**Application Android native en Kotlin**, indépendante du portail web WarLike Hub.

## Fonctionnalités de la version 0.1

- Accueil Android natif, sombre, optimisé pour téléphone et tablette.
- Catalogue local de **14 applications et services WarLike** fourni avec l'APK dans `app/src/main/assets/apps.json`.
- Recherche, filtres par usage et favoris conservés sur le téléphone via `SharedPreferences`.
- Ouverture explicite des services HTTPS via les intents Android.
- Aucun navigateur intégré, pas de WebView ni de PWA.
- Les services web ne sont **pas** annoncés comme APK Android autonomes.

## Compiler un APK

Ouvrir ce dépôt directement dans Android Studio, avec **JDK 17** et **Android SDK 35**. Synchroniser le projet Gradle puis choisir **Build → Build APK(s)**.

Alternativement, avec Gradle 8.9 disponible :

```sh
gradle :app:assembleDebug
```

Le fichier attendu sera `app/build/outputs/apk/debug/app-debug.apk`.

Le workflow GitHub Actions `.github/workflows/android.yml` construit également un APK debug et l'expose comme *artefact* du workflow, sous réserve d'une compilation réussie.

## Limites et prochaines étapes

Il faut encore **exécuter la compilation CI**, tester sur Android, puis créer une version release **signée** pour distribution. Les logiciels WarLike sont majoritairement des services Web ; le Hub natif peut les lancer, mais ne prétend pas disposer d'APK pour chacun.

Les futures applications Android WarLike devront déclarer leurs identifiants de package, signatures, versions et APK officiels. Le Hub n'installe ni n'exécute de code arbitraire et ne demande aucune permission de gestion globale des packages.
