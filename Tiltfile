custom_build(
    # Name of the container image
     ref = 'catalog-service',

    # Command to build the container image do container
    command = './gradlew bootBuildImage --imageName $EXPECTED_REF',

    # Arquivos a serem observados que acionam uma novam compilação
    deps = ['build.gradle', 'src']
)

#Implantar
k8s_yaml(kustomize('k8s'))
# Manage port
k8s_resource('catalog-service', port_forwards=['9001'])
