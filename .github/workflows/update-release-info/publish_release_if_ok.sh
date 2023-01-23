#!/bin/bash

# Expecting params:
#  - RELEASE_VERSION

if [[ -z "$RELEASE_VERSION" ]]; then
  echo "ERROR: RELEASE_VERSION environment value not defined"
  exit 1
fi

echo "*** Check release '$RELEASE_VERSION' ***"
REPO=timo-laasonen/spring-boot-file-uploader-deploy

# Specific release can be queried only by tag and we are using tag
# for specific version => we need to list all releases and check names
# to find tag

TAG_NAME=$(gh release list --repo timo-laasonen/spring-boot-file-uploader-deploy | grep -oE $RELEASE_VERSION$'\t''Draft'$'\t''(.*)'$'\t' | cut -f 3)
echo "TAG_NAME=$TAG_NAME"

if [[ -z "$TAG_NAME" ]]; then
  echo "ERROR: Draft release '$RELEASE_VERSION' not found or doesn't have a tag"
  exit 1
fi

BODY=$(gh release view --repo timo-laasonen/spring-boot-file-uploader-deploy $TAG_NAME --json body --jq .body)
echo "BODY=$BODY"

if [[ -z "$BODY" ]]; then
  echo "ERROR: Release '$RELEASE_VERSION' has no body"
  exit 1
fi

XXX_FOUND=$(echo "$BODY" | grep "=xxx")
if [[ -n "$XXX_FOUND" ]]; then
  echo "There are still subcomponent tags undefined. Not publishing release"
else
  echo "All tags defined, publishing release"

  gh release edit "$TAG_NAME" \
    --repo timo-laasonen/spring-boot-file-uploader-deploy \
    --draft=false
fi
