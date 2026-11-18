const formatadorMoeda = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });

export function formatarMoeda(valor) {
    return formatadorMoeda.format(Number(valor ?? 0));
}

export function formatarData(dataIso) {
    if (!dataIso) {
        return '-';
    }
    const [ano, mes, dia] = dataIso.substring(0, 10).split('-');
    return `${dia}/${mes}/${ano}`;
}

export function dataDeHojeIso() {
    const hoje = new Date();
    hoje.setMinutes(hoje.getMinutes() - hoje.getTimezoneOffset());
    return hoje.toISOString().substring(0, 10);
}

export function escaparHtml(texto) {
    const elemento = document.createElement('div');
    elemento.textContent = texto ?? '';
    return elemento.innerHTML;
}

export function mostrarMensagem(elemento, texto, tipo = 'sucesso', detalhes = []) {
    const itensDetalhe = detalhes.map((detalhe) => `<li>${escaparHtml(detalhe)}</li>`).join('');
    elemento.innerHTML = escaparHtml(texto) + (itensDetalhe ? `<ul>${itensDetalhe}</ul>` : '');
    elemento.className = `mensagem visivel ${tipo}`;
}

export function mostrarErro(elemento, erro) {
    mostrarMensagem(elemento, erro.message, 'erro', erro.detalhes ?? []);
}

export function esconderMensagem(elemento) {
    elemento.className = 'mensagem';
    elemento.textContent = '';
}

export function preencherSelect(select, opcoes, textoPadrao) {
    const opcaoPadrao = `<option value="">${escaparHtml(textoPadrao)}</option>`;
    const demaisOpcoes = opcoes
        .map(({ valor, descricao }) => `<option value="${escaparHtml(String(valor))}">${escaparHtml(descricao)}</option>`)
        .join('');
    select.innerHTML = opcaoPadrao + demaisOpcoes;
}
