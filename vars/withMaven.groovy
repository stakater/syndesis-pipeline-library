#!/usr/bin/groovy

def call(Map parameters = [:], body) {

    def defaultLabel = buildId(prefix : 'maven')
    def label = parameters.get('label', defaultLabel)

    def mavenImage = parameters.get('mavenImage', 'maven:3.3.9')
    def persistent = parameters.get('persistent', true)
    def inheritFrom = parameters.get('inheritFrom', 'base')
    def serviceAccount = parameters.get('serviceAccount', '')
    def workingDir = parameters.get('workingDir', '/home/jenkins')

    if (persistent) {
        podTemplate(label: label, inheritFrom: "${inheritFrom}", serviceAccount: "${serviceAccount}",
                containers: [containerTemplate(name: 'maven', image: "${mavenImage}", command: '/bin/sh -c', args: 'cat', ttyEnabled: true, envVars: [containerEnvVar(key: 'MAVEN_OPTS', value: "-Dmaven.repo.local=${workingDir}/.mvnrepository/")])],
                volumes: [persistentVolumeClaim(claimName: 'm2-local-repo', mountPath: "/${workingDir}/.mvnrepository")]) {
            body()
        }
    } else {
        podTemplate(label: label, inheritFrom: "${inheritFrom}", serviceAccount: "${serviceAccount}",
                containers: [containerTemplate(name: 'maven', image: "${mavenImage}", command: '/bin/sh -c', args: 'cat', ttyEnabled: true)]) {
            body()
        }
    }
}
