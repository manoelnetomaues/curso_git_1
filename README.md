# Viva Mulher

Aplicativo Android local-first para registrar e consultar percepções de segurança em corridas por aplicativo.

## Compatibilidade

- Android 13 a Android 16 (`minSdk 33`, `targetSdk 36`)
- Projeto Java sem bibliotecas de terceiros

## Recursos do MVP

- Cadastro de empresa, primeiro nome do motorista e placa
- Nota de segurança de 1 (perigo extremo) a 10 (muito confiável)
- Avaliação durante ou após a corrida
- Pesquisa por placa completa
- Exibição pública mascarada da placa
- Lista local ordenada por risco
- Botão para abrir o discador com o número 190
- Avisos sobre relatos comunitários e uso responsável

## Compilar

Abra a pasta no Android Studio com Android SDK 36 instalado e selecione
`Build > Build APK(s)`. O APK de teste será criado em
`app/build/outputs/apk/debug/app-debug.apk`.

## Próxima etapa: sincronização

A versão 1.0 salva apenas no aparelho. Um serviço online exige autenticação,
moderação, política de privacidade, canal de contestação e regras contra abuso
antes de receber dados reais de motoristas.
