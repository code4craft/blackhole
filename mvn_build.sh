mvn clean package dependency:copy-dependencies -DoutputDirectory=target/lib
cd scripts/
./deploy.sh
sudo ./test.sh

