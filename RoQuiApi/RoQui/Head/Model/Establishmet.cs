namespace RoQuiApi.RoQui.Head.Model;

using System.ComponentModel.DataAnnotations.Schema;

[Table("establishments")]
public class Establishment
{
    [Column("id")]
    public int Id { get; set; }
    [Column("code", TypeName = "varchar")]
    public required string Code { get; set; }

    [Column("business_name", TypeName = "varchar")]
    public string? BusinessName { get; set; }

    [Column("address", TypeName = "varchar")]
    public required string Address { get; set; }

    [Column("is_principal", TypeName = "boolean")]
    public required bool IsPrincipal { get; set; }

    [Column("taxpayer_id")]
    public int TaxpayerId { get; set; }

    public required Taxpayer Taxpayer { get; set; }
}