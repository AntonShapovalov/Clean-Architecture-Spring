# Serve Angular frontend
FROM nginx:alpine
COPY frontend/nginx.conf /etc/nginx/conf.d/default.conf
COPY release/frontend.zip /tmp/frontend.zip
RUN unzip /tmp/frontend.zip -d /tmp \
    && mv /tmp/frontend/dist/omdb-frontend/browser/* /usr/share/nginx/html/ \
    && rm -rf /tmp/frontend /tmp/frontend.zip
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
