* `docker pull minio/minio`
* `docker run -p 9000:9000 -p 9001:9001 --name minio -e "MINIO_ROOT_USER=minioadmin" -e "MINIO_ROOT_PASSWORD=minioadmin" -v /mydata/minio/data:/data -v /mydata/minio/config:/root/.minio -d minio/minio server /data --console-address ":9001"`
* `http://localhost:9001/browser`
* `не забыть в ui minio создать Access Keys (которые потом надо указать в app.yml) и нужный bucket из app.yml`