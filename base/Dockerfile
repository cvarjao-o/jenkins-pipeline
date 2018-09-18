FROM jenkins/core/there

USER 0
RUN find /opt/jenkins/jobs/ -mindepth 1 -maxdepth 1 -type d -not -name 'ON_*' -exec rm -rf '{}' \;
USER 1001

COPY ./configuration /opt/jenkins/

