#!/bin/bash

# Expecting params:
#  - RELEASE_VERSION
#  - COMPONENT
#  - CHECK_EXISTENCE

if [[ -z "$RELEASE_VERSION" ]]; then
  echo "ERROR: RELEASE_VERSION environment value not defined"
  exit 1
fi

if [[ -z "$COMPONENT" ]]; then
  echo "ERROR: COMPONENT environment value not defined"
  exit 1
fi

if [[ -z "$CHECK_EXISTENCE" ]]; then
  echo "ERROR: CHECK_EXISTENCE environment value not defined"
  exit 1
fi

if [[ "$COMPONENT" == "all" ]]; then
  COMPONENTS=(frontend backend deploy)
else
  COMPONENTS=($COMPONENT)
fi

ERRORS=false
for C in "${COMPONENTS[@]}"; do
  echo "*** Check release '$RELEASE_VERSION' for component '$C' ***"
  REPO=timo-laasonen/spring-boot-file-uploader-$C

  if [[ "$C" == "deploy" ]]; then
    NAME=$RELEASE_VERSION
  else
    NAME=$C-$RELEASE_VERSION
  fi

  echo "----- Releases"
  GH_OUTPUT=$(gh release list --repo $REPO)
  echo "$GH_OUTPUT"
  echo "-----"

  # try to match to name that is unique, public or draft
  GREP_OUTPUT=$(echo "$GH_OUTPUT" | grep -o "^$NAME"$'\t')
  echo "GREP_OUTPUT=$GREP_OUTPUT"

  if [[ -z "$GREP_OUTPUT" ]]; then
    echo "Release '$RELEASE_VERSION' not found"
    if [[ "$CHECK_EXISTENCE" == "true" ]]; then
      echo "ERROR: Release should EXISTS"
      ERRORS=true
    fi
  else
      echo "Release $RELEASE_VERSION found for '$C'."
      if [[ "$CHECK_EXISTENCE" != "true" ]]; then
        echo "ERROR: Release should NOT exists"
        ERRORS=true
      fi
    fi

done

if [[ "$ERRORS" == "true" ]]; then
  echo "ERROR: There were errors. If release exists but should not, you can manually delete releases."
  exit 1
fi
