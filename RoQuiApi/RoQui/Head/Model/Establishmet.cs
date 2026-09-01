using System.ComponentModel.DataAnnotations.Schema;

namespace RoQuiApi.RoQui.Head.Model;

[Table("establishments")]
public class Establishmet
{
    public int Id { get; set; }
    [Column("code", TypeName = "varchar")]
    public required string Code { get; set; }

    [Column("business_name", TypeName = "varchar")]
    public required string BusinessName { get; set; }

    [Column("address", TypeName = "varchar")]
    public required string Address { get; set; }

    [Column("principal", TypeName = "varchar")]
    public required string Principal { get; set; }

    [Column("taxpayer_id")]
    public int TaxpayerId { get; set; }

    public required Taxpayer Taxpayer { get; set; }
}