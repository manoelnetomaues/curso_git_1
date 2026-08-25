# Viva Mulher

Aplicativo Android local-first para registrar e consultar percepções de segurança em corridas por aplicativo.

## Compatibilidade

- Android 13 a Android 16 (`minSdk 33`, `targetSdk 36`)
- Projeto Java sem bibliotecas de terceiros

## Recursos da versão 3

- Cadastro de empresa, primeiro nome do motorista e placa
- Nota de segurança de 1 (perigo extremo) a 10 (muito confiável)
- Avaliação durante ou após a corrida
- Pesquisa por placa completa
- Exibição pública mascarada da placa
- Lista local ordenada por risco
- Botão para abrir o discador com o número 190
- Avisos sobre relatos comunitários e uso responsável
- Layout em formato de rede social com feed
- Perfil com foto, apelido e biografia opcional
- Dez temas: cinco neutros e cinco fofinhos
- Ranking semanal calculado a partir dos relatos locais
- Área educativa sobre leis, conceitos e mulheres importantes
- Botão de pânico com gravação local, localização, mensagem aos três contatos e discagem para 190
- Avatar original de segurança da mulher
- Cidade, estado e país em cada avaliação
- Totais positivos e negativos e estados com maior ocorrência
- Dimensões responsivas em dp para respeitar fontes grandes do Android

## Compilar

Abra a pasta no Android Studio com Android SDK 36 instalado e selecione
`Build > Build APK(s)`. O APK de teste será criado em
`app/build/outputs/apk/debug/app-debug.apk`.

## Próxima etapa: sincronização

A versão 2.0 salva apenas no aparelho. Um serviço online exige autenticação,
moderação, política de privacidade, canal de contestação e regras contra abuso
antes de receber dados reais de motoristas.
