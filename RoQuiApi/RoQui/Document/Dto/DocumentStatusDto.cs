namespace RoQuiApi.RoQui.Document.Dto;

using System.ComponentModel.DataAnnotations;

public class DocumentStatusDto
{
    [Required]
    public required string Code { get; set; }

    [Required]
    public required string Number { get; set; }

    // Oracle solo manda code y number cuando pregunta. Estos cuatro van de vuelta,
    // por eso no llevan Required: si los pides, rechaza la consulta.
    public string? AuthorizationCode { get; set; }

    public DateTime? AuthorizationDate { get; set; }

    public string? Observation { get; set; }

    public string? Status { get; set; }
}
