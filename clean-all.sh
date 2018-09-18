#!/usr/bin/env bash
oc delete dc,bc,is,route,pod,service,secrets,pvc,configmap -l '!template,!system' -n csnr-devops-lab-tools
oc delete RoleBinding,ServiceAccount -l 'app' -n csnr-devops-lab-tools
