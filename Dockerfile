FROM ubuntu:latest
LABEL authors="honag"

ENTRYPOINT ["top", "-b"]