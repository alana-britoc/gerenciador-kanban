import { useEffect, useRef, useState } from 'react'
import { ChevronDown, Flag, Search, Shapes, Tag, X } from 'lucide-react'
import { FILTROS_VAZIOS, SEM_PRIORIDADE, etiquetasDe, temFiltro } from '../filtros'
import { TIPOS } from '../tipos'

const OPCOES_TIPO = Object.entries(TIPOS).map(([valor, { nome, Icone }]) => ({ valor, nome, Icone }))

const OPCOES_PRIORIDADE = [
  { valor: 'ALTA', nome: 'Alta', cor: '#c75a66' },
  { valor: 'MEDIA', nome: 'Média', cor: '#c87d38' },
  { valor: 'BAIXA', nome: 'Baixa', cor: '#4e9a5c' },
  { valor: SEM_PRIORIDADE, nome: 'Sem prioridade', cor: 'var(--claro)' },
]

function alternar(lista, valor) {
  return lista.includes(valor) ? lista.filter((v) => v !== valor) : [...lista, valor]
}

function campoDeTexto(elemento) {
  return elemento.closest('input, textarea, select, [contenteditable="true"]')
}

function Opcoes({ rotulo, Icone, opcoes, selecionadas, onMudar, vazio }) {
  const [aberto, setAberto] = useState(false)
  const caixa = useRef(null)
  const quantidade = selecionadas.length

  useEffect(() => {
    if (!aberto) return
    const clicouFora = (e) => {
      if (!caixa.current?.contains(e.target)) setAberto(false)
    }
    const apertouEsc = (e) => {
      if (e.key === 'Escape') setAberto(false)
    }
    window.addEventListener('pointerdown', clicouFora)
    window.addEventListener('keydown', apertouEsc)
    return () => {
      window.removeEventListener('pointerdown', clicouFora)
      window.removeEventListener('keydown', apertouEsc)
    }
  }, [aberto])

  return (
    <div className="filtro" ref={caixa}>
      <button
        type="button"
        className={`pilula-filtro${quantidade > 0 ? ' ativa' : ''}`}
        aria-expanded={aberto}
        onClick={() => setAberto((v) => !v)}
      >
        <Icone size={14} /> {rotulo}
        {quantidade > 0 && <span className="filtro-contador">{quantidade}</span>}
        <ChevronDown size={14} className="filtro-seta" />
      </button>
      {aberto && (
        <fieldset className="filtro-opcoes">
          <legend className="so-leitor">{rotulo}</legend>
          {opcoes.length === 0 ? (
            <p className="painel-vazio">{vazio}</p>
          ) : (
            opcoes.map(({ valor, nome, cor, Icone: IconeOpcao }) => (
              <label key={valor} className="filtro-opcao">
                <input
                  type="checkbox"
                  checked={selecionadas.includes(valor)}
                  onChange={() => onMudar(alternar(selecionadas, valor))}
                />
                {IconeOpcao && <IconeOpcao size={13} className="filtro-icone" />}
                {cor && <span className="ponto" style={{ '--cor-coluna': cor }} />}
                {nome}
              </label>
            ))
          )}
        </fieldset>
      )}
    </div>
  )
}

export default function Filtros({ cartoes, filtros, onMudar, visiveis }) {
  const busca = useRef(null)
  const etiquetas = etiquetasDe(cartoes).map((e) => ({ valor: e, nome: e }))
  const ativo = temFiltro(filtros)
  const mudar = (campo) => (valor) => onMudar({ ...filtros, [campo]: valor })

  useEffect(() => {
    const atalho = (e) => {
      if (e.key !== '/' || campoDeTexto(e.target) || document.querySelector('dialog[open]')) return
      e.preventDefault()
      busca.current?.focus()
    }
    window.addEventListener('keydown', atalho)
    return () => window.removeEventListener('keydown', atalho)
  }, [])

  return (
    <div className="filtros" role="search">
      <label className="busca">
        <Search size={15} />
        <input
          ref={busca}
          type="search"
          value={filtros.texto}
          onChange={(e) => mudar('texto')(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === 'Escape') {
              mudar('texto')('')
              e.currentTarget.blur()
            }
          }}
          placeholder="Buscar cartões"
          aria-label="Buscar no título e na descrição"
        />
        {filtros.texto ? (
          <button type="button" aria-label="Limpar busca" onClick={() => mudar('texto')('')}>
            <X size={13} />
          </button>
        ) : (
          <kbd aria-hidden="true">/</kbd>
        )}
      </label>

      <Opcoes rotulo="Tipo" Icone={Shapes} opcoes={OPCOES_TIPO} selecionadas={filtros.tipos} onMudar={mudar('tipos')} />
      <Opcoes
        rotulo="Prioridade"
        Icone={Flag}
        opcoes={OPCOES_PRIORIDADE}
        selecionadas={filtros.prioridades}
        onMudar={mudar('prioridades')}
      />
      <Opcoes
        rotulo="Etiqueta"
        Icone={Tag}
        opcoes={etiquetas}
        selecionadas={filtros.etiquetas}
        onMudar={mudar('etiquetas')}
        vazio="Nenhum cartão tem etiqueta ainda"
      />

      {ativo && (
        <div className="filtros-resumo">
          <span aria-live="polite">
            {visiveis} de {cartoes.length} {cartoes.length === 1 ? 'cartão' : 'cartões'}
          </span>
          <button type="button" className="botao-texto" onClick={() => onMudar(FILTROS_VAZIOS)}>
            <X size={13} /> Limpar filtros
          </button>
        </div>
      )}
    </div>
  )
}
