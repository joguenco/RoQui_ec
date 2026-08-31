using Microsoft.EntityFrameworkCore.Migrations;
using Npgsql.EntityFrameworkCore.PostgreSQL.Metadata;

#nullable disable

namespace RoQuiApi.Migrations
{
    /// <inheritdoc />
    public partial class init : Migration
    {
        /// <inheritdoc />
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "taxpayers",
                columns: table => new
                {
                    id = table.Column<int>(type: "integer", nullable: false)
                        .Annotation("Npgsql:ValueGenerationStrategy", NpgsqlValueGenerationStrategy.IdentityByDefaultColumn),
                    identification = table.Column<string>(type: "varchar", nullable: false),
                    legal_name = table.Column<string>(type: "varchar", nullable: false),
                    forced_accounting = table.Column<string>(type: "varchar", nullable: false),
                    special_taxpayer = table.Column<string>(type: "varchar", nullable: true),
                    retention_agent = table.Column<string>(type: "varchar", nullable: true),
                    rimpe = table.Column<string>(type: "varchar", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_taxpayers", x => x.id);
                });
        }

        /// <inheritdoc />
        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "taxpayers");
        }
    }
}
