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
openssl pkcs12 \
    -export \
    -in "$DATA"/localhost.crt \
    -inkey "$DATA"/localhost.key \
    -out "$DATA"/localhost.p12 \
    -passout pass:changeit
keytool -importkeystore \
    -srckeystore "$DATA"/localhost.p12 \
    -srcstoretype PKCS12 \
    -destkeystore "$DATA"/localhost.jks \
    -deststoretype JKS
```

Tomcat
-

Get the latest version of `server.xml`:

```
wget -P data https://raw.githubusercontent.com/apache/tomcat/main/conf/server.xml
```

Remove default `Connector` and replace with

```
    <Connector
      protocol="HTTP/1.1"
      port="8443"
      maxThreads="200"
      maxParameterCount="1000"
      scheme="https"
      secure="true"
      SSLEnabled="true"
      keystoreFile="/usr/local/tomcat/conf/localhost.jks"
      clientAuth="false"
      sslProtocol="TLS"/>
```

Run 
=

```
docker run -p 8443:8443 -v ./data/:/usr/local/tomcat/conf tomcat:9.0
```

Install `data/root-ca.crt` in your Browser and open https://localhost:8443: You get a Tomcat "Not Found" page but the certificate is valid or run `curl -v --cacert data/root-ca.crt https://localhost:8443` to achieve the same on the command line.