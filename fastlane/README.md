# Fastlane

This directory provides lanes for Android release tasks.

## Setup

1. Install dependencies:
    ```sh
    gem install fastlane
    ```
2. Export a base64 encoded Play Store service account to `SERVICE_ACCOUNT_JSON`.

## Bump version

Increment `VERSION_NAME` and `versionCode`:

```sh
fastlane bump_version
```

## Publish bundle

Upload an Android App Bundle to Google Play. The lane removes the decoded
service account file when finished.

```sh
fastlane publish aab:path/to/app.aab update_priority:3 track:internal
```

If `aab` is omitted, the lane uses the production bundle under
`androidApp/build/outputs/bundle/productionRelease/`.

`update_priority` may also come from the `UPDATE_PRIORITY` environment variable.

