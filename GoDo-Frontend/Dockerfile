# Stage 1: Build
FROM node:20-alpine AS build

WORKDIR /app

# copy dependencies and install them to cache layers, by doing this we make sure that dependencies don't get installed
# every time a line of css/html/ts has been changed and only if the dependencies change which makes the container start up
# much faster
COPY package*.json ./
RUN npm ci

# copy the source code
COPY . .

# builds the angular application (the html page, script bundles and stylesheets)
RUN npm run build -- --configuration=development

# Stage 2: Serve with Nginx
FROM nginx:alpine

# copy the build from the app and run it with nginx to minimize the image size by removing node_modules which can take up
# huge amounts of space
COPY --from=build /app/dist/go-do-frontend/browser /usr/nginx/html

COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
