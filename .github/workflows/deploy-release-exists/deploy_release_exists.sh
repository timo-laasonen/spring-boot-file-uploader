#!/bin/bash

# Expecting params:
#  - RELEASE_VERSION
#  - COMPONENT

if [[ -z "$RELEASE_VERSION" ]]; then
  echo "ERROR: RELEASE_VERSION environment value not defined"
  exit 1
fi

if [[ -z "$COMPONENT" ]]; then
  echo "ERROR: COMPONENT environment value not defined"
  exit 1
fi

echo "*** Check release '$RELEASE_VERSION' ***"
REPO=timo-laasonen/spring-boot-file-uploader-deploy

# Specific release can be queried only by tag and we are using tag
# for specific version => we need to list all releases and check names
# to find tag

RELEASES=$(gh release list --repo timo-laasonen/spring-boot-file-uploader-deploy)
echo "-----"
echo "Releases:"
echo "$RELEASES"
echo "-----"
# If you are running gh commands from command line, e.g. for troubleshooting,
# note that console shows parenthesis around tag, but they are not in captured
# stdout.

TAG_NAME=$(echo "$RELEASES" | grep -oE $RELEASE_VERSION$'\t''Draft'$'\t''(.*)'$'\t' | cut -f 3)
echo "TAG_NAME=$TAG_NAME"

if [[ -z "$TAG_NAME" ]]; then
  echo "ERROR: Draft release '$RELEASE_VERSION' not found or doesn't have a release tag"
  exit 1
fi

BODY=$(gh release view --repo timo-laasonen/spring-boot-file-uploader-deploy $TAG_NAME --json body --jq .body)
echo "-----"
echo "BODY=$BODY"
echo "-----"

if [[ -z "$BODY" ]]; then
  echo "ERROR: Release '$RELEASE_VERSION' has no body"
  exit 1
fi

# based on component use different value property name that we are defined
if [[ "$COMPONENT" == "frontend" ]]; then
  PROP_NAME="FRONTEND_CONTAINER_IMAGE_TAG"
elif [[ "$COMPONENT" == "backend" ]]; then
  PROP_NAME="BACKEND_CONTAINER_IMAGE_TAG"
elif [[ "$COMPONENT" == "deploy" ]]; then
  PROP_NAME="DEPLOY_GIT_TAG"
else
  echo "ERROR: Unknown component '$COMPONENT'"
  exit 1
fi

# check that there is xxx, i.e. value is not defined, in other words overwrite
# is not allowed
XXX_FOUND=$(echo "$BODY" | grep "${PROP_NAME}=xxx")
if [[ -z "$XXX_FOUND" ]]; then
  echo "ERROR: No xxx tag value found for '$COMPONENT' in body '$BODY'"
  echo "Once set value can't be overwritten. If needed you may manually edit release body."
  exit 1
fi

echo "OK"
