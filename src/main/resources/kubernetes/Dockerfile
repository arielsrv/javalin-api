ARG GO_VERSION=1.24.4
FROM golang:${GO_VERSION} AS build
WORKDIR /app
COPY go.mod go.sum ./
RUN --mount=type=cache,target=/go/pkg/mod \
    --mount=type=secret,id=netrc,target=/root/.netrc \
    sh -c 'go mod download -x && go mod verify'
COPY . .
ARG COMMIT_HASH
RUN --mount=type=cache,target=/go/pkg/mod \
    CGO_ENABLED=0 go build -v -ldflags="-s -w -X 'main.CommitHash=${COMMIT_HASH}'" -trimpath -o app ./src

FROM gcr.io/distroless/static-debian12:debug AS release
COPY --from=build /app /home/nonroot/release
WORKDIR /home/nonroot/release
USER nonroot:nonroot
ENTRYPOINT ["./app"]
