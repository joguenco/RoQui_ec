namespace RoQuiApi.RoQui.Security;

using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Filters;
using RoQuiApi.RoQui.Shared;

public class ApiKeyAuthorizationFilter : IAuthorizationFilter
{
    public const string ApiKeyHeaderName = "X-API-KEY";

    private readonly IApiKeyValidator _apiKeyValidator;

    public ApiKeyAuthorizationFilter(IApiKeyValidator apiKeyValidator)
    {
        _apiKeyValidator = apiKeyValidator;
    }

    public void OnAuthorization(AuthorizationFilterContext context)
    {
        var requestApiKey = context.HttpContext.Request.Headers[ApiKeyHeaderName].FirstOrDefault();

        // Corta la peticion antes de que llegue al controlador, asi no se toca la base.
        if (!_apiKeyValidator.IsValid(requestApiKey))
        {
            context.Result = new UnauthorizedObjectResult(
                new MessageDto { Title = "Invalid API Key", Status = 401 });
        }
    }
}
