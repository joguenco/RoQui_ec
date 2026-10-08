namespace RoQuiApi.Util;

public static class OpenApiConfig
{
    public static IServiceCollection AddOpenApiConfig(this IServiceCollection services)
    {
        services.AddOpenApi(options =>
        {
            string[] tagOrder =
            [
                "Ping",
                "Version",
                "Taxpayer",
                "Invoice",
                "CreditNote",
                "DebitNote",
                "Liquidation",
                "Withhold"
            ];

            options.AddDocumentTransformer((document, context, cancellationToken) =>
            {
                if (document.Tags is { Count: > 0 })
                {
                    var sortedTags = document.Tags
                        .OrderBy(tag =>
                        {
                            var position = Array.IndexOf(tagOrder, tag.Name);
                            return position < 0 ? int.MaxValue : position;
                        })
                        .ThenBy(tag => tag.Name)
                        .ToList();

                    document.Tags.Clear();
                    foreach (var tag in sortedTags)
                    {
                        document.Tags.Add(tag);
                    }
                }

                return Task.CompletedTask;
            });
        });

        return services;
    }
}