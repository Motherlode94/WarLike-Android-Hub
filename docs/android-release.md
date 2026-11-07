# Release Android Hub : sécurité et validation

## Préparer une signature

Sur une machine de confiance, créer et conserver une clé de signature release de manière sécurisée. **Ne jamais committer de keystore, mot de passe ou fichier APK signé avec des clés non autorisées.**

Configurer quatre secrets GitHub Actions dans le dépôt :
- `WARLIKE_KEYSTORE_BASE64` : contenu du fichier keystore encodé en Base64 (sans retour de ligne)
- `WARLIKE_KEYSTORE_PASSWORD` : mot de passe du keystore
- `WARLIKE_KEY_ALIAS` : alias de la clé
- `WARLIKE_KEY_PASSWORD` : mot de passe de la clé

Lancer le workflow **Android APK — WarLike Hub** via `workflow_dispatch`, option `release_signed=true`. La compilation release échoue si une valeur est absente. **L'APK n'est pas considéré comme signé tant que le build et la vérification de signature n'ont pas réussi.**

## Catalogue APK et mises à jour

Le catalogue natif `app/src/main/assets/android-apps.json` est actuellement vide : aucun APK individuel WarLike vérifié n'est encore publié. Pour chaque future application Android WarLike, ajouter explicitement : `id`, `name`, `packageName` (préfixe `com.warlike.`), `versionCode`, `downloadUrl` (fichier APK sur GitHub officiel), `sha256` (64 caractères hexadécimaux).

Le Hub valide l'identifiant du package et la source HTTPS, télécharge avec DownloadManager, vérifie SHA-256 avant de passer le fichier à l'installateur Android. Android demande toujours l'accord explicite de l'utilisateur, et le système vérifie la signature au moment de la mise à jour. Le Hub n'installe rien silencieusement et ne contourne pas les contrôles Android.

## Tests

Le workflow construit l'APK debug ; une tâche d'émulateur exécute `connectedDebugAndroidTest` (catalogue et validation des sources). Il reste indispensable de tester un **téléphone Android physique** : installation, navigation, rotation, permissions système, batterie, réseau mobile, téléchargement, mise à jour officielle, retour arrière, et lecture des erreurs.
