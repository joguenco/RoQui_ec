namespace RoQuiApi.RoQui.Security;

using Microsoft.AspNetCore.Mvc;

// Se pone [ApiKey] encima del controlador o del metodo y ya exige la cabecera.
// Hereda de ServiceFilterAttribute para que el filtro pueda pedir el repositorio
// por inyeccion, que es lo que necesita para leer la clave de la base.
[AttributeUsage(AttributeTargets.Class | AttributeTargets.Method)]
public class ApiKeyAttribute : ServiceFilterAttribute
{
    public ApiKeyAttribute() : base(typeof(ApiKeyAuthorizationFilter))
    {
    }
}
