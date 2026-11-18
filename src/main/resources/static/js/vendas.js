import { api } from './api.js';
import { iniciarPaginaProtegida } from './layout.js';
import { PaginaCrud } from './paginaCrud.js';
import { dataDeHojeIso, formatarData, formatarMoeda, preencherSelect } from './ui.js';

const campo = (id) => document.getElementById(id);

let livrosPorId = new Map();

async function carregarLivros() {
    const selecionado = campo('livroId').value;
    const livros = await api.get('/livros');
    livrosPorId = new Map(livros.map((livro) => [String(livro.id), livro]));

    const opcoes = livros.map((livro) => ({
        valor: livro.id,
        descricao: `${livro.titulo} — ${formatarMoeda(livro.preco)} (estoque: ${livro.quantidadeEstoque})`,
    }));
    preencherSelect(campo('livroId'), opcoes, 'Selecione um livro');
    campo('livroId').value = selecionado;
}

function atualizarTotalPrevisto() {
    const livro = livrosPorId.get(campo('livroId').value);
    const quantidade = Number(campo('quantidade').value) || 0;
    const total = livro ? livro.preco * quantidade : 0;
    campo('valorTotalPrevisto').textContent = `Total previsto: ${formatarMoeda(total)}`;
}

async function iniciar() {
    if (!iniciarPaginaProtegida('vendas')) {
        return;
    }
    preencherSelect(campo('formaPagamento'), await api.get('/catalogos/formas-pagamento'), 'Selecione');
    await carregarLivros();

    campo('livroId').addEventListener('change', atualizarTotalPrevisto);
    campo('quantidade').addEventListener('input', atualizarTotalPrevisto);

    const pagina = new PaginaCrud({
        recurso: '/vendas',
        nomeRegistro: 'Venda',
        nomeFeminino: true,
        colunas: [
            { valor: (venda) => formatarData(venda.dataVenda) },
            { valor: (venda) => venda.nomeCliente },
            { valor: (venda) => venda.tituloLivro },
            { valor: (venda) => venda.quantidade },
            { valor: (venda) => formatarMoeda(venda.valorUnitario) },
            { valor: (venda) => formatarMoeda(venda.valorTotal) },
            { valor: (venda) => venda.formaPagamentoDescricao },
        ],
        lerFormulario: () => ({
            livroId: campo('livroId').value || null,
            nomeCliente: campo('nomeCliente').value.trim(),
            quantidade: campo('quantidade').value || null,
            formaPagamento: campo('formaPagamento').value || null,
            dataVenda: campo('dataVenda').value || null,
        }),
        preencherFormulario: (venda) => {
            campo('livroId').value = venda.livroId;
            campo('nomeCliente').value = venda.nomeCliente;
            campo('quantidade').value = venda.quantidade;
            campo('formaPagamento').value = venda.formaPagamento;
            campo('dataVenda').value = venda.dataVenda;
            atualizarTotalPrevisto();
        },
        descreverRegistro: (venda) => `a venda de "${venda.tituloLivro}" para ${venda.nomeCliente}`,
        aoLimparFormulario: () => {
            campo('dataVenda').value = dataDeHojeIso();
            atualizarTotalPrevisto();
        },
        aoAlterarDados: carregarLivros,
    });
    await pagina.iniciar();
}

iniciar();
