namespace RoQuiApi.RoQui.Document.Dto;

using System.ComponentModel.DataAnnotations;

public class PaymentDto
{
    [Required]
    public required string Code { get; set; }

    [Required]
    public required decimal Total { get; set; }

    public decimal? Deadline { get; set; }

    public string? UnitTime { get; set; }
}