
app {
    name = 'jenkins-pipeline'
    namespaces {
        'build'{
            namespace = 'csnr-devops-lab-tools'
            disposable = true
        }
        'dev' {
            namespace = app.namespaces.'build'.namespace
            disposable = true
        }
        'prod' {
            namespace = app.namespaces.'build'.namespace
            disposable = false
        }
    }

    git {
        workDir = ['git', 'rev-parse', '--show-toplevel'].execute().text.trim()
        uri = ['git', 'config', '--get', 'remote.origin.url'].execute().text.trim()
        ref = "refs/pull/${opt.'pr'}/head"
        commit = ['git', 'rev-parse', 'HEAD'].execute().text.trim()
    }

    build {
        env {
            name = "build"
            id = "pr-${opt.'pr'}"
        }
        version = "${vars.deployment.version}" //app-version  and tag
        name = "${opt.'build-name'?:app.name}"
        suffix = "-build-${opt.'pr'}"
        id = "${app.name}${app.build.suffix}"
        namespace = app.namespaces.'build'.namespace
        timeoutInSeconds = 60*20 // 20 minutes
        templates = [
            [
                'file':'.pipeline/jenkins.bc.json',
                'params':[
                    'NAME': "${app.build.name}",
                    'SUFFIX': "${app.build.suffix}",
                    'VERSION': app.build.version,
                    'SOURCE_REPOSITORY_URL': app.git.uri,
                    'SOURCE_REPOSITORY_REF': app.git.ref
                ]
            ]
        ]
    }

    deployment {
        env {
            name = vars.deployment.env.name // env-name
            id = vars.deployment.env.id
        }
        version = "${vars.deployment.version}" //app-version  and tag
        name = "${vars.deployment.name}" //app-name   (same name accross all deployments)
        suffix = "${vars.deployment.suffix}" // app (unique name across all deployments int he namespace)
        id = "${app.deployment.name}${app.deployment.suffix}" // app (unique name across all deployments int he namespace)
        namespace = "${vars.deployment.namespace}"
        
        timeoutInSeconds = 60*20 // 20 minutes
        templates = [
                [
                    'file':'https://raw.githubusercontent.com/cvarjao-o/openshift-templates/8764b3b92d7e5497c089ef48c5c260e9b8cb3d5f/jenkins/jenkins.dc.yaml',
                    'params':[
                        'NAME':app.deployment.name,
                        'BC_NAME':app.build.name,
                        'NAME_SUFFIX':app.deployment.suffix,
                        'VERSION': app.deployment.version,
                        'MASTER_CPU_REQUEST': '300m',
                        'MASTER_CPU_LIMIT': '500m',
                        'SLAVE_CPU_REQUEST': '300m',
                        'SLAVE_CPU_LIMIT': '500m',
                        'ROUTE_HOST': 'jenkinns-hello-cvarjao.pathfinder.gov.bc.ca'
                    ]
                ]
        ]
    }
}

environments {
    'dev' {
        vars {
            deployment {
                env {
                    name ="dev"
                    id = "pr-${opt.'pr'}"
                }
                suffix = "-dev-${opt.'pr'}"
                name = "${opt.'deployment-name'?:app.name}"
                namespace = app.namespaces[env.name].namespace
                version = "${vars.deployment.name}-${vars.deployment.env.name}-v${opt.'pr'}" //app-version  and tag
            }
        }
    }
    'test' {
        vars {
            deployment {
                env {
                    name ="test"
                    id = "pr-${opt.'pr'}"
                }
                suffix = '-test'
                name = "${opt.'deployment-name'?:app.name}"
                namespace = app.namespaces[env.name].namespace
                version = "${vars.deployment.name}-${vars.deployment.env.name}" //app-version  and tag
            }
        }
    }
    'prod' {
        vars {
            deployment {
                env {
                    name ="prod"
                    id = "pr-${opt.'pr'}"
                }
                suffix = ''
                id = "${app.name}${vars.deployment.suffix}"
                name = "${opt.'deployment-name'?:app.name}"
                namespace = app.namespaces[env.name].namespace
                version = "${vars.deployment.name}-${vars.deployment.env.name}" //app-version  and tag
            }
        }
    }
}
