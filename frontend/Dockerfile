# Serve Angular frontend
FROM nginx:alpine
COPY release/frontend.zip /tmp/frontend.zip
RUN unzip /tmp/frontend.zip -d /tmp \
    && mv /tmp/dist/omdb-frontend/browser/* /usr/share/nginx/html/ \
    && rm -rf /tmp/dist /tmp/frontend.zip
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
