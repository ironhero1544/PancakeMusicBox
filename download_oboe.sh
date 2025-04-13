#!/bin/bash

# Oboe 라이브러리 다운로드 스크립트
OBOE_VERSION="1.8.5" # 최신 안정 버전
OBOE_DIR="app/src/main/cpp/oboe"

# 이미 다운로드되어 있는지 확인
if [ -d "$OBOE_DIR" ]; then
    echo "Oboe directory already exists at $OBOE_DIR"
    exit 0
fi

# 디렉토리 생성
mkdir -p app/src/main/cpp

# Oboe 다운로드
echo "Downloading Oboe v$OBOE_VERSION..."
git clone --depth 1 --branch $OBOE_VERSION https://github.com/google/oboe.git $OBOE_DIR

echo "Oboe successfully downloaded to $OBOE_DIR"
