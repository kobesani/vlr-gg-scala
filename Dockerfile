FROM ubuntu:22.04

USER root

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
        curl \
        python3.10 \
        openjdk-11-jdk \
        openjdk-11-jre \
    && apt-get autoremove -yqq --purge \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

RUN useradd --create-home vlrscrape

USER vlrscrape

WORKDIR /home/vlrscrape

RUN curl -fL https://github.com/coursier/launchers/raw/master/cs-x86_64-pc-linux.gz |\
    gzip -d > cs && chmod +x cs && eval "$(./cs setup --env)"

RUN mkdir -p /home/vlrscrape/build

COPY ./build.sc /home/vlrscrape/build/
COPY ./mill /home/vlrscrape/build/
ADD ./foo /home/vlrscrape/build/foo

WORKDIR /home/vlrscrape/build
RUN ./mill foo.assembly

ENTRYPOINT ["/bin/bash"]
