namespace RoQuiApi.RoQui.Security;

public interface IApiKeyValidator
{
    bool IsValid(string? requestApiKey);
}
