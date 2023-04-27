Setup
=

Certificates
-

```
DATA=data
rm -rf "$DATA"
mkdir -p "$DATA"

# Root CA
openssl genrsa \
    -out "$DATA"/root-ca.key \
    4096
openssl req \
    -x509 \
    -new \
    -nodes \
    -key "$DATA"/root-ca.key \
    -sha256 \
    -days 1024 \
    -out "$DATA"/root-ca.crt \
    -subj "/CN=Root CA"

# Localhost
openssl genrsa \
    -out "$DATA"/localhost.key \
    4096
openssl req \
    -new \
    -sha256 \
    -key "$DATA"/localhost.key \
    -subj "/CN=localhost" \
    -config <(cat /etc/ssl/openssl.cnf <(printf "[SAN]\nsubjectAltName=DNS:localhost")) \
    -reqexts SAN \
    -out "$DATA"/localhost.csr
openssl x509 \
    -req \
    -in "$DATA"/localhost.csr \
    -CA "$DATA"/root-ca.crt \
    -CAkey "$DATA"/root-ca.key \
    -CAcreateserial \
    -extfile <(cat /etc/ssl/openssl.cnf <(printf "[SAN]\nsubjectAltName=DNS:localhost")) \
    -extensions SAN \
    -sha256 \
    -days 500 \
    -out "$DATA"/localhost.crt
```

Run
=

```
docker run -p 8443:443 -v ./data:/etc/tls -v ./conf:/etc/nginx -v ./src:/var/www nginx
```

Install `data/root-ca.crt` in your Browser and open https://localhost:8443: You see the page `src/index.html` or
run `curl -v --cacert data/root-ca.crt https://localhost:8443` to achieve the same on the command line.