# RoQuiApi.Test

## Generate coverage report 
```
dotnet test --collect:"XPlat Code Coverage"
```
```
dotnet tool install -g dotnet-reportgenerator-globaltool
```
```
reportgenerator -reports:"**/coverage.cobertura.xml" -targetdir:"coveragereport" -reporttypes:Html
```
Open index.html in coveragereport folder