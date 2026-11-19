export default function Seletor({ rotulo, opcoes, valor, onMudar }) {
  return (
    <div role="radiogroup" aria-label={rotulo} className="seletor">
      {opcoes.map((opcao) => (
        <button
          key={opcao.valor}
          type="button"
          role="radio"
          aria-checked={opcao.valor === valor}
          onClick={() => onMudar(opcao.valor)}
        >
          {opcao.nome}
        </button>
      ))}
    </div>
  )
}
