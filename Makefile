run-server:
	./gradlew :server:run

db-shell:
	mysql -u root -ppassword -h 127.0.0.1 -P 3306 -D corefin_db --protocol=tcp
db-clean:
	./gradlew flywayClean
db-migrate:
	./gradlew flywayMigrate -i
gen-migration:
	./scripts/gen_db_migration.sh
docker-stubs:
	docker-compose up

