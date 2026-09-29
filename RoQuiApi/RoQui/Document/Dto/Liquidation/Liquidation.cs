namespace RoQuiApi.RoQui.Invoice.Dto;

using System.ComponentModel.DataAnnotations;
using RoQuiApi.RoQui.Shared;

public class LiquidationDto
{
    [Required]
    public required string Code { get; set; }

    [Required]
    public required string Number { get; set; }

    [Required]
    public required DateTime Date { get; set; }

    [Required]
    public required string IdentificationType { get; set; }

    [Required]
    public required string Identification { get; set; }

    [Required]
    public required string LegalName { get; set; }

    [Required]
    public required string Address { get; set; }

    [Required]
    [AccessKey]
    public required string AccessKey { get; set; }

    [Required]
    [MinLength(1, ErrorMessage = "The liquidation must contain at least one detail.")]
    public virtual required ICollection<LiquidationDetailDto> LiquidationDetails { get; set; }
}
