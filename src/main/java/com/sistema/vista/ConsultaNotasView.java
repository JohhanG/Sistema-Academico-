package com.sistema.vista;

import com.sistema.modelo.Estudiante;
import com.sistema.modelo.EstudianteDao;
import com.sistema.modelo.InscripcionCurso;
import com.sistema.modelo.Usuario;
import com.sistema.servicio.InscripcionCursoService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Optional;

// Vista estilo portal academico UNIAJC para consultar las materias y notas de un estudiante
public class ConsultaNotasView extends JFrame implements ActionListener {

    private static final Color AZUL_OSCURO = new Color(11, 56, 97);
    private static final Color AMARILLO_UNIAJC = new Color(242, 183, 5);
    private static final Color GRIS_ENCABEZADO = new Color(206, 212, 218);
    private static final Color GRIS_BORDE = new Color(220, 224, 230);

    private final Usuario usuarioActual;
    private final JFrame menuPrincipal;
    private final EstudianteDao estudianteDao;
    private final InscripcionCursoService inscripcionService;

    private JPanel panelSelectorAdmin;
    private JComboBox<String> cmbEstudiantes;
    private JButton btnConsultarEstudiante;

    private JLabel lblValIdentificacion;
    private JLabel lblValNombre;
    private JLabel lblValCorreo;
    private JLabel lblValSituacion;
    private JLabel lblValCreditos;
    private JLabel lblValPromedio;

    private JTable tblNotas;
    private DefaultTableModel modeloTabla;
    private JButton btnRefrescar;
    private JButton btnVolverMenu;

    public ConsultaNotasView(Usuario usuarioActual, JFrame menuPrincipal) {
        this.usuarioActual = usuarioActual;
        this.menuPrincipal = menuPrincipal;
        this.estudianteDao = new EstudianteDao();
        this.inscripcionService = new InscripcionCursoService();

        initComponents();
        cargarDatosSegunRol();
    }

    private void initComponents() {
        setTitle("Sistema Academico - Consultar Notas Actuales");
        setSize(980, 600);
        setMinimumSize(new Dimension(860, 520));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                volverAlMenu();
            }
        });

        JPanel panelRaiz = new JPanel(new BorderLayout(8, 8));
        panelRaiz.setBackground(Color.WHITE);
        panelRaiz.setBorder(new EmptyBorder(10, 12, 10, 12));

        // Barra superior azul oscuro
        JPanel barraTitulo = new JPanel(new BorderLayout());
        barraTitulo.setBackground(AZUL_OSCURO);
        barraTitulo.setPreferredSize(new Dimension(0, 34));
        JLabel lblBarra = new JLabel("Consultar Notas Actuales", SwingConstants.CENTER);
        lblBarra.setForeground(Color.WHITE);
        lblBarra.setFont(new Font("Segoe UI", Font.BOLD, 14));
        barraTitulo.add(lblBarra, BorderLayout.CENTER);

        // Panel superior completo (barra + selector opcional + ficha del estudiante)
        JPanel panelNorte = new JPanel();
        panelNorte.setLayout(new BoxLayout(panelNorte, BoxLayout.Y_AXIS));
        panelNorte.setBackground(Color.WHITE);
        panelNorte.add(barraTitulo);
        panelNorte.add(Box.createVerticalStrut(6));

        // Selector de estudiante (visible solo para ADMIN y DOCENTE)
        panelSelectorAdmin = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        panelSelectorAdmin.setBackground(new Color(245, 247, 250));
        panelSelectorAdmin.setBorder(new LineBorder(GRIS_BORDE, 1));
        panelSelectorAdmin.add(new JLabel("Seleccionar Estudiante:"));
        cmbEstudiantes = new JComboBox<>();
        cmbEstudiantes.setPreferredSize(new Dimension(300, 26));
        panelSelectorAdmin.add(cmbEstudiantes);
        btnConsultarEstudiante = new JButton("Ver Boletin");
        btnConsultarEstudiante.addActionListener(this);
        panelSelectorAdmin.add(btnConsultarEstudiante);
        panelNorte.add(panelSelectorAdmin);
        panelNorte.add(Box.createVerticalStrut(6));

        // Fila 1: Identificacion y Nombre (Encabezado amarillo)
        JPanel fila1 = new JPanel(new GridLayout(1, 2, 4, 0));
        fila1.setBackground(Color.WHITE);
        lblValIdentificacion = crearValorFicha("-");
        lblValNombre = crearValorFicha("-");
        fila1.add(crearBloqueFicha("Codigo / ID Estudiante", lblValIdentificacion, AMARILLO_UNIAJC));
        fila1.add(crearBloqueFicha("Nombre del Estudiante", lblValNombre, AMARILLO_UNIAJC));
        panelNorte.add(fila1);
        panelNorte.add(Box.createVerticalStrut(4));

        // Fila 2: Correo y Situacion (Encabezado gris)
        JPanel fila2 = new JPanel(new GridLayout(1, 2, 4, 0));
        fila2.setBackground(Color.WHITE);
        lblValCorreo = crearValorFicha("-");
        lblValSituacion = crearValorFicha("ACTIVO");
        fila2.add(crearBloqueFicha("Correo Institucional", lblValCorreo, GRIS_ENCABEZADO));
        fila2.add(crearBloqueFicha("Situacion Academica", lblValSituacion, GRIS_ENCABEZADO));
        panelNorte.add(fila2);
        panelNorte.add(Box.createVerticalStrut(4));

        // Fila 3: Programa, Creditos y Promedio (Encabezado gris)
        JPanel fila3 = new JPanel(new GridLayout(1, 3, 4, 0));
        fila3.setBackground(Color.WHITE);
        JLabel lblValPrograma = crearValorFicha("TECNOLOGIA EN SISTEMAS DE INFORMACION");
        lblValCreditos = crearValorFicha("0");
        lblValPromedio = crearValorFicha("0.0");
        fila3.add(crearBloqueFicha("Programa Academico", lblValPrograma, GRIS_ENCABEZADO));
        fila3.add(crearBloqueFicha("Creditos Matriculados", lblValCreditos, GRIS_ENCABEZADO));
        fila3.add(crearBloqueFicha("Promedio Acumulado", lblValPromedio, GRIS_ENCABEZADO));
        panelNorte.add(fila3);
        panelNorte.add(Box.createVerticalStrut(8));

        // Barra amarilla de seccion "Materias"
        JPanel barraMaterias = new JPanel(new BorderLayout());
        barraMaterias.setBackground(AMARILLO_UNIAJC);
        barraMaterias.setPreferredSize(new Dimension(0, 26));
        JLabel lblMaterias = new JLabel("Materias Matriculadas", SwingConstants.CENTER);
        lblMaterias.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblMaterias.setForeground(Color.BLACK);
        barraMaterias.add(lblMaterias, BorderLayout.CENTER);
        panelNorte.add(barraMaterias);

        panelRaiz.add(panelNorte, BorderLayout.NORTH);

        // Tabla central de materias y notas
        String[] columnas = {
                "Codigo", "Descripcion (Materia)", "Creditos", "Docente",
                "Aula y Horario", "Nota Final", "Definitiva", "Estado"
        };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblNotas = new JTable(modeloTabla);
        tblNotas.setRowHeight(26);
        tblNotas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblNotas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tblNotas.getTableHeader().setBackground(GRIS_ENCABEZADO);
        tblNotas.setGridColor(GRIS_BORDE);

        // Renderizador para pintar en rojo las notas menores a 3.0 como en el portal
        DefaultTableCellRenderer renderNota = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                c.setForeground(Color.BLACK);
                if (value != null) {
                    try {
                        double nota = Double.parseDouble(value.toString());
                        if (nota < 3.0) {
                            c.setForeground(new Color(200, 30, 30));
                        } else {
                            c.setForeground(new Color(20, 120, 40));
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
                return c;
            }
        };
        tblNotas.getColumnModel().getColumn(5).setCellRenderer(renderNota);
        tblNotas.getColumnModel().getColumn(6).setCellRenderer(renderNota);

        JScrollPane scrollTabla = new JScrollPane(tblNotas);
        scrollTabla.setBorder(new LineBorder(GRIS_BORDE, 1));
        panelRaiz.add(scrollTabla, BorderLayout.CENTER);

        // Barra inferior con botones azules oscuros
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        panelSur.setBackground(Color.WHITE);

        btnRefrescar = crearBotonPortal("Actualizar Notas");
        btnVolverMenu = crearBotonPortal("Volver al Menu Principal");

        btnRefrescar.addActionListener(this);
        btnVolverMenu.addActionListener(this);

        panelSur.add(btnRefrescar);
        panelSur.add(btnVolverMenu);
        panelRaiz.add(panelSur, BorderLayout.SOUTH);

        setContentPane(panelRaiz);
    }

    private JPanel crearBloqueFicha(String titulo, JLabel lblValor, Color colorEncabezado) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new LineBorder(GRIS_BORDE, 1));
        panel.setBackground(Color.WHITE);

        JLabel lblHeader = new JLabel(titulo, SwingConstants.CENTER);
        lblHeader.setOpaque(true);
        lblHeader.setBackground(colorEncabezado);
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblHeader.setPreferredSize(new Dimension(0, 22));

        lblValor.setPreferredSize(new Dimension(0, 26));
        panel.add(lblHeader, BorderLayout.NORTH);
        panel.add(lblValor, BorderLayout.CENTER);
        return panel;
    }

    private JLabel crearValorFicha(String texto) {
        JLabel lbl = new JLabel(texto, SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lbl.setBackground(Color.WHITE);
        lbl.setOpaque(true);
        return lbl;
    }

    private JButton crearBotonPortal(String texto) {
        JButton btn = new JButton(texto);
        btn.setBackground(AZUL_OSCURO);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void cargarDatosSegunRol() {
        String rol = usuarioActual != null && usuarioActual.getRol() != null
                ? usuarioActual.getRol().toUpperCase()
                : "ESTUDIANTE";

        cmbEstudiantes.removeAllItems();
        modeloTabla.setRowCount(0);
        lblValIdentificacion.setText("");
        lblValNombre.setText("Sin datos disponibles");
        lblValCorreo.setText("");
        lblValCreditos.setText("0");
        lblValPromedio.setText("Sin calificar");

        if (rol.equals("ESTUDIANTE")) {
            panelSelectorAdmin.setVisible(false);
            try {
                Estudiante est = new com.sistema.servicio.IdentidadAcademicaService(estudianteDao)
                        .exigirEstudiante(usuarioActual);
                mostrarBoletinDeEstudiante(est);
            } catch (IllegalArgumentException e) {
                modeloTabla.setRowCount(0);
                lblValNombre.setText("Sin estudiante asociado");
                lblValPromedio.setText("Sin datos");
                lblValCorreo.setText("");
                lblValIdentificacion.setText("");
                javax.swing.JOptionPane.showMessageDialog(this, e.getMessage(),
                        "Cuenta sin vinculo valido", javax.swing.JOptionPane.WARNING_MESSAGE);
            }
        } else {
            List<Estudiante> todos;
            if (rol.equals("DOCENTE")) {
                try {
                    java.util.Set<Integer> ids = new java.util.LinkedHashSet<>();
                    for (InscripcionCurso i : inscripcionService.listarParaUsuario(usuarioActual)) {
                        ids.add(i.getIdEstudiante());
                    }
                    todos = new java.util.ArrayList<>();
                    for (int id : ids) estudianteDao.buscarPorId(id).ifPresent(todos::add);
                } catch (IllegalArgumentException e) {
                    javax.swing.JOptionPane.showMessageDialog(this, e.getMessage());
                    todos = java.util.Collections.emptyList();
                }
            } else if (rol.equals("ADMIN")) {
                todos = estudianteDao.obtenerTodos();
            } else {
                todos = java.util.Collections.emptyList();
            }
            for (Estudiante e : todos) {
                cmbEstudiantes.addItem(e.getIdEstudiante() + " - " + e.getNombre() + " " + e.getApellido());
            }
            panelSelectorAdmin.setVisible(true);
            if (!todos.isEmpty()) {
                mostrarBoletinDeEstudiante(todos.get(0));
            }
        }
    }

    private void mostrarBoletinDeEstudiante(Estudiante est) {
        lblValIdentificacion.setText("EST-" + String.format("%04d", est.getIdEstudiante()));
        lblValNombre.setText((est.getNombre() + " " + est.getApellido()).toUpperCase());
        lblValCorreo.setText(est.getEmail());

        modeloTabla.setRowCount(0);
        List<InscripcionCurso> materias = inscripcionService.listarParaUsuario(usuarioActual).stream()
                .filter(i -> i.getIdEstudiante() == est.getIdEstudiante())
                .collect(java.util.stream.Collectors.toList());

        int totalCreditos = 0;
        double sumaNotas = 0.0;
        int materiasCalificadas = 0;

        for (InscripcionCurso insc : materias) {
            totalCreditos += insc.getCreditos();
            String codigoCurso = String.format("MAT-%03d", insc.getIdGrupo());
            String notaTexto = "-";
            if (insc.getNotaFinal() != null) {
                notaTexto = String.valueOf(insc.getNotaFinal());
                sumaNotas += insc.getNotaFinal();
                materiasCalificadas++;
            }

            Object[] fila = {
                    codigoCurso,
                    insc.getNombreMateria() != null ? insc.getNombreMateria().toUpperCase() : "GRUPO " + insc.getIdGrupo(),
                    insc.getCreditos(),
                    insc.getNombreDocente() != null ? insc.getNombreDocente() : "-",
                    insc.getAulaHorario() != null ? insc.getAulaHorario() : "-",
                    notaTexto,
                    notaTexto,
                    insc.getEstado()
            };
            modeloTabla.addRow(fila);
        }

        lblValCreditos.setText(String.valueOf(totalCreditos));
        if (materiasCalificadas > 0) {
            double promedio = sumaNotas / materiasCalificadas;
            lblValPromedio.setText(String.format("%.2f", promedio));
        } else {
            lblValPromedio.setText("Sin calificar");
        }
    }

    private void consultarSeleccionadoEnCombo() {
        String item = (String) cmbEstudiantes.getSelectedItem();
        if (item == null) return;
        String[] partes = item.split(" - ");
        int idEst = Integer.parseInt(partes[0].trim());
        Optional<Estudiante> opt = estudianteDao.buscarPorId(idEst);
        if (opt.isPresent()) {
            mostrarBoletinDeEstudiante(opt.get());
        }
    }

    private void volverAlMenu() {
        dispose();
        if (menuPrincipal != null) {
            menuPrincipal.setVisible(true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == btnRefrescar) {
            cargarDatosSegunRol();
        } else if (source == btnConsultarEstudiante) {
            if (panelSelectorAdmin.isVisible()) {
                consultarSeleccionadoEnCombo();
            } else {
                cargarDatosSegunRol();
            }
        } else if (source == btnVolverMenu) {
            volverAlMenu();
        }
    }
}
