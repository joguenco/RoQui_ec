namespace RoQuiApi.Data;

using Microsoft.EntityFrameworkCore;
using RoQui.Head.Model;
using RoQui.Invoice.Model;



public class AppDbContext : DbContext
{
    public AppDbContext(DbContextOptions<AppDbContext> opt) : base(opt)
    {

    }

    public DbSet<Taxpayer> Taxpayers { get; set; }
    public DbSet<Establishment> Establishments { get; set; }
    public DbSet<Invoice> Invoices { get; set; }
    public DbSet<InvoiceDetail> InvoiceDetails { get; set; }
    public DbSet<InvoiceDetailTax> InvoiceDetailTaxes { get; set; }


    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        //ForeignKey constraints
        modelBuilder.Entity<Taxpayer>()
            .HasMany(t => t.Establishments)
            .WithOne(e => e.Taxpayer)
            .HasForeignKey(e => e.TaxpayerId)
            .OnDelete(DeleteBehavior.Cascade);

        modelBuilder.Entity<Invoice>()
            .HasMany(i => i.InvoiceDetails)
            .WithOne(d => d.Invoice)
            .HasForeignKey(d => d.InvoiceId)
            .OnDelete(DeleteBehavior.Cascade);

        modelBuilder.Entity<InvoiceDetail>()
            .HasMany(d => d.InvoiceDetailTaxes)
            .WithOne(t => t.InvoiceDetail)
            .HasForeignKey(t => t.InvoiceDetailId)
            .OnDelete(DeleteBehavior.Cascade);

        //Unique constraints
        modelBuilder.Entity<Invoice>()
            .HasIndex(i => new { i.Code, i.Number })
            .IsUnique();

        modelBuilder.Entity<Invoice>()
            .HasIndex(i => i.AccessKey)
            .IsUnique();
    }
}
