FROM alpine:3.14

ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en' LC_ALL='en_US.UTF-8'

RUN apk add --no-cache tzdata musl-locales musl-locales-lang \
    && rm -rf /var/cache/apk/*

ENV JAVA_VERSION jdk16u

RUN set -eux; \
    apk add --no-cache --virtual .fetch-deps curl; \
    ARCH="$(apk --print-arch)"; \
    case "${ARCH}" in \
       amd64|x86_64) \
         ESUM='9115d9ae0c4441308c5ea8d32329a0eebb7b77e8f05e025c1a3755a6b3b773de'; \
         BINARY_URL='https://github.com/AdoptOpenJDK/openjdk16-binaries/releases/download/jdk16u-2021-05-08-12-45/OpenJDK16U-jre_x64_alpine-linux_hotspot_2021-05-07-23-30.tar.gz'; \
         ;; \
       *) \
         echo "Unsupported arch: ${ARCH}"; \
         exit 1; \
         ;; \
    esac; \
    curl -LfsSo /tmp/openjdk.tar.gz ${BINARY_URL}; \
    echo "${ESUM} */tmp/openjdk.tar.gz" | sha256sum -c -; \
    mkdir -p /opt/java/openjdk; \
    cd /opt/java/openjdk; \
    tar -xf /tmp/openjdk.tar.gz --strip-components=1; \
    apk del --purge .fetch-deps; \
    rm -rf /var/cache/apk/*; \
    rm -rf /tmp/openjdk.tar.gz;

ENV JAVA_HOME=/opt/java/openjdk \
    PATH="/opt/java/openjdk/bin:$PATH"

EXPOSE 8097
COPY build/libs/ratelimiter-*.jar ratelimiter.jar
COPY docker/entrypoint.sh /entrypoint.sh

RUN grep '^networkaddress.cache.ttl=' $JAVA_HOME/conf/security/java.security || echo 'networkaddress.cache.ttl=5' >> $JAVA_HOME/conf/security/java.security

ENTRYPOINT ["/entrypoint.sh"]