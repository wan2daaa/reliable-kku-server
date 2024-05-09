.PHONY: run

SSH_USER := wane
SSH_HOST := 192.168.15.7
SSH_KEY := ~/.ssh/id_rsa


run:

	@echo "build gradlew"
	@./gradlew clean build

	@echo "build docker image with platform linux/amd64"
	@docker buildx build --platform linux/amd64 --load -t reliable-kku-server:1.0.0 -f ./Dockerfile-dev .

	@echo "docker save"
	@docker save reliable-kku-server:1.0.0 > ./reliable-kku-server.tar

	@echo "scp reliable-kku-server.tar to server"
	@scp -i ${SSH_KEY} ./reliable-kku-server.tar ${SSH_USER}@${SSH_HOST}:~/reliable-kku-server

	@echo "scp docker-compose.yml to server"
	@scp -i ${SSH_KEY} ./docker-compose.yml ${SSH_USER}@${SSH_HOST}:~/reliable-kku-server

	@echo "scp docker-compose.yml to server"
	@scp -i ${SSH_KEY} ./docker-compose.yml ${SSH_USER}@${SSH_HOST}:~/reliable-kku-server

	@echo "scp .env to server"
	@scp -i ${SSH_KEY} ./.env ${SSH_USER}@${SSH_HOST}:~/reliable-kku-server

	@echo "scp prometheus.yml to server"
	@scp -i ${SSH_KEY} ./prometheus/prometheus.yml ${SSH_USER}@${SSH_HOST}:~/reliable-kku-server/prometheus

	@echo "load docker file"
	@ssh -i ${SSH_KEY} ${SSH_USER}@${SSH_HOST} 'cd ~/reliable-kku-server && docker load < ./reliable-kku-server.tar'

	@echo "Running docker-compose up -d"
	@ssh -i ${SSH_KEY} ${SSH_USER}@${SSH_HOST} 'cd ~/reliable-kku-server && docker-compose up -d'

	@echo "remove docker tar file"
	@rm ./reliable-kku-server.tar

	@echo "Done!"