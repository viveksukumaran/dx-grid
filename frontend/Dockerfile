# base image
FROM node:latest

ENV production_flag true
ENV backend_url http://localhost:8080/
ENV page_sizes "[10, 25, 50, 100]"

RUN apt-get update

# set working directory
WORKDIR /app

# add `/app/node_modules/.bin` to $PATH
ENV PATH /app/node_modules/.bin:$PATH

# install and cache app dependencies
COPY package.json package-lock.* /app/
RUN npm install
RUN npm install -g @angular/cli@7.1.0

# add app
COPY . /app

EXPOSE 4200

# start app
CMD ./run.sh
