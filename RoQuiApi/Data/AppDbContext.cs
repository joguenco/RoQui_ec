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


    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
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
    }
}
