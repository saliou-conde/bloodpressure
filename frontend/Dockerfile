# 1. Angular Build Phase
FROM node:22-alpine AS build

WORKDIR /app

COPY package.json package-lock.json ./
RUN npm install

COPY . .

RUN npx ng build frontend --configuration production

# 2. Nginx Runtime Phase
FROM nginx:alpine

COPY --from=build /app/dist/frontend /usr/share/nginx/html

COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
