import { api } from './api.js';
import { iniciarPaginaProtegida } from './layout.js';
import { PaginaCrud } from './paginaCrud.js';
import { formatarData, formatarMoeda, preencherSelect } from './ui.js';

const campo = (id) => document.getElementById(id);

async function iniciar() {
    if (!iniciarPaginaProtegida('livros')) {
        return;
    }
    preencherSelect(campo('categoria'), await api.get('/catalogos/categorias-livro'), 'Selecione');

    const pagina = new PaginaCrud({
        recurso: '/livros',
        nomeRegistro: 'Livro',
        colunas: [
            { valor: (livro) => livro.titulo },
            { valor: (livro) => livro.autor },
            { valor: (livro) => livro.isbn },
            { valor: (livro) => livro.categoriaDescricao },
            { valor: (livro) => formatarMoeda(livro.preco) },
            { valor: (livro) => livro.quantidadeEstoque },
            { valor: (livro) => formatarData(livro.dataPublicacao) },
        ],
        lerFormulario: () => ({
            titulo: campo('titulo').value.trim(),
            autor: campo('autor').value.trim(),
            isbn: campo('isbn').value.trim(),
            categoria: campo('categoria').value || null,
            preco: campo('preco').value || null,
            quantidadeEstoque: campo('quantidadeEstoque').value || null,
            dataPublicacao: campo('dataPublicacao').value || null,
        }),
        preencherFormulario: (livro) => {
            campo('titulo').value = livro.titulo;
            campo('autor').value = livro.autor;
            campo('isbn').value = livro.isbn;
            campo('categoria').value = livro.categoria;
            campo('preco').value = livro.preco;
            campo('quantidadeEstoque').value = livro.quantidadeEstoque;
            campo('dataPublicacao').value = livro.dataPublicacao ?? '';
        },
        descreverRegistro: (livro) => `o livro "${livro.titulo}"`,
    });
    await pagina.iniciar();
}

iniciar();
