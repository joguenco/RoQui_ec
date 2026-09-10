using System.ComponentModel.DataAnnotations.Schema;

namespace RoQuiApi.RoQui.Electronic.Model;

[Table("ele_documents")]
public class Electronic
{
    [Column("id", TypeName = "bigint")]
    public long Id { get; set; }

    [Column("code", TypeName = "varchar")]
    public required string Code { get; set; }

    [Column("number", TypeName = "varchar")]
    public required string Number { get; set; }

    [Column("authorization_code", TypeName = "varchar")]
    public string? AuthorizationCode { get; set; }

    [Column("authorization_date", TypeName = "timestamp without time zone")]
    public DateTime? AuthorizationDate { get; set; }

    [Column("observation", TypeName = "varchar")]
    public string? Observation { get; set; }

    [Column("status", TypeName = "varchar")]
    public string? Status { get; set; }
}
